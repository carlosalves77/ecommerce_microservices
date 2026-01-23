package com.carldev.payment_service.service;

import com.carldev.payment_service.dto.request.ItemDTO;
import com.carldev.payment_service.dto.response.GetPaymentsResponseDTO;
import com.carldev.payment_service.entity.Payment;
import com.carldev.payment_service.entity.PaymentItem;
import com.carldev.payment_service.dto.request.OrderConsumeEvent;
import com.carldev.payment_service.kafka.producer.PaymentSuccessEvent;
import com.carldev.payment_service.mapper.PaymentMapper;
import com.carldev.payment_service.repository.PaymentRepository;
import com.carldev.payment_service.utils.PaymentStatus;
import com.carldev.payment_service.utils.PaymentType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodListParams;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@CrossOrigin
public class PaymentConsumerService {


    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final PaymentMapper paymentMapper;
    Event event;

    @Value("${STRIPE_API_KEY}")
    private String stripeApiKey;

    @Value("${WEBHOOK_SECRET_KEY}")
    private String webhookSecretKey;


    public PaymentConsumerService(PaymentRepository paymentRepository,
                                  ApplicationEventPublisher eventPublisher, ObjectMapper objectMapper
            , PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
        this.paymentMapper = paymentMapper;
    }

    @PostConstruct
    public void createApiKey() {
        Stripe.apiKey = stripeApiKey;

    }

    @KafkaListener(
            topics = "checkout-create",
            groupId = "checkout-consumer")
    public void createPayment(String messageJson) throws JsonProcessingException {

        OrderConsumeEvent orderConsumeEvent = objectMapper.readValue(
                messageJson, OrderConsumeEvent.class
        );

        Payment payment = new Payment();

        payment.setUserId(orderConsumeEvent.userId());
        payment.setAmount(orderConsumeEvent.totalAmount());
        payment.setEmail(orderConsumeEvent.email());
        payment.setUserName(orderConsumeEvent.userName());
        payment.setOrderNumber(orderConsumeEvent.orderNumber());
        payment.setPaymentStatus(PaymentStatus.PENDING);

        orderConsumeEvent.items().forEach(
                item -> {
                    PaymentItem paymentItem = new PaymentItem();
                    paymentItem.setSku(item.sku());
                    paymentItem.setQuantity(item.quantity());
                    paymentItem.setUnitPrice(item.unitPrice());
                    payment.addItem(paymentItem);
                }
        );

        paymentRepository.save(payment);
    }


    @Transactional()
    public Page<GetPaymentsResponseDTO> getPayment(int pageNumber) {

        int pageOne = pageNumber - 1;

        Pageable page = PageRequest.of(pageOne, 10);

        Page<Payment> allPayments = paymentRepository.findAll(page);

        return allPayments.map(paymentMapper::toDto);
    }

    @Transactional
    public void handlePayment(UUID paymentId, String customerId) {

        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new RuntimeException("Pagamento não encontrado")
        );

        if (payment.getPaymentStatus() == PaymentStatus.APPROVED) {
            throw new RuntimeException("Pagamento já foi aprovado");
        }

        try {
            BigDecimal totalValue = payment.getAmount().multiply(new BigDecimal("100"));

            String itemJson;
            try {
                itemJson = objectMapper.writeValueAsString(payment.getItems());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }


            Map<String, String> metadata = new HashMap<>();
            metadata.put("userId", String.valueOf(payment.getUserId()));
            metadata.put("id", String.valueOf(payment.getId()));
            metadata.put("userName", payment.getUserName());
            metadata.put("email", payment.getEmail());
            metadata.put("amount", payment.getAmount().toString());
            metadata.put("orderNumber", payment.getOrderNumber().toString());
            metadata.put("items", itemJson);

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(totalValue.longValue())
                    .setCurrency("brl")
                    .putAllMetadata(metadata)
                    .setCustomer(customerId)
                    .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams
                                    .AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);


            PaymentMethodListParams listParams = PaymentMethodListParams.builder()
                    .setCustomer(customerId)
                    .setType(PaymentMethodListParams.Type.CARD)
                    .build();

            PaymentMethodCollection paymentMethods = PaymentMethod.list(listParams);

            if (paymentMethods.getData().isEmpty()) {
                throw new RuntimeException("Cliente não possui cartão vinculado");
            }

            String savedPaymentMethod = paymentMethods.getData().get(0).getId();

            PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
                    .setPaymentMethod(savedPaymentMethod)
                    .setReturnUrl("http://localhost:4007/api/payment/success-mock")
                    .build();


            paymentIntent.confirm(confirmParams);

        } catch (StripeException e) {
            log.error("Erro ao criar pagamento no Stripe", e);
        }

    }

    public void deleteById(String id) {

        Payment payment = paymentRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado")
        );

        paymentRepository.deleteById(payment.getId());
    }

    @Transactional
    public void webhook(String payload, String sigHeader) throws StripeException {

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecretKey);
        } catch (SignatureVerificationException e) {
            log.info("Erro de assinatura {} ", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Erro no Webhook" + e.getMessage());
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

        StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);

        if (stripeObject == null) {
            log.error("Falha ao desserializar objeto");
            return;
        }

        if (!(stripeObject instanceof PaymentIntent)) {
            log.debug("Evento ignorado: {} - Tipo: {}", event.getType(),
                    stripeObject.getClass().getSimpleName());
            return;
        }

        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        Map<String, String> metadata = paymentIntent.getMetadata();

        if (metadata == null || !metadata.containsKey("id")) {
            log.warn("Webhook recebido sem metados ou ID");
            return;
        }

        String paymentIdStr = metadata.get("id");

        if (paymentIdStr == null) {
            log.warn("Webhook recebida sem Id de pagamento");
            return;
        }

        UUID paymentId = UUID.fromString(paymentIdStr);
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new RuntimeException("Pagamento não encontrado: " + paymentId)
        );

        log.info("Qual o tipo de evento {}",event.getType());

        switch (event.getType()) {
            case "payment_intent.succeeded":
                handleSuccess(payment, paymentIntent);
                break;
            case "payment_intent.payment_failed":
                handleFailure(payment, paymentIntent);
                break;

            default:
                log.info("Evento não tratado: {}", event.getType());
        }
    }

    private void handleSuccess(Payment payment, PaymentIntent paymentIntent) {

        if (payment.getPaymentStatus() == PaymentStatus.APPROVED) {
            log.info("Evento duplicado: " + payment.getId());
            return;
        }

        String last4 = "";
        String paymentMethodType = "desconhecido";

        String chargeId = paymentIntent.getLatestCharge();
        if (chargeId != null) {
            try {
                Charge charge = Charge.retrieve(chargeId);
                if (charge.getPaymentMethodDetails() != null) {
                    paymentMethodType = charge.getPaymentMethodDetails().getType();
                    if (charge.getPaymentMethodDetails().getCard() != null) {
                        last4 = charge.getPaymentMethodDetails().getCard().getLast4();
                    }
                }

            } catch (StripeException e) {
                log.error("Error ao buscar charge" + e.getMessage());
            }
        }

        payment.setPaymentStatus(PaymentStatus.APPROVED);
        payment.setUpdatedAt(Instant.now());

        try {
            List<ItemDTO> listOfItems = objectMapper.readValue(
                    paymentIntent.getMetadata().get("items"),
                    new TypeReference<List<ItemDTO>>() {
                    }
            );

            PaymentSuccessEvent event = new PaymentSuccessEvent(
                    payment.getUserId(),
                    payment.getEmail(),
                    payment.getUserName(),
                    payment.getOrderNumber(),
                    payment.getAmount().toString(),
                    paymentIntent.getCurrency(),
                    paymentMethodType,
                    last4,
                    String.valueOf(Instant.now()),
                    listOfItems
            );

            updatePaymentType(payment, paymentMethodType);
            eventPublisher.publishEvent(event);

        } catch (JsonProcessingException e) {
            log.error("Erro ao ler Json de itens: " + e.getMessage());
        }

        paymentRepository.save(payment);
    }

    private void handleFailure(Payment payment, PaymentIntent paymentIntent) {

        if (payment.getPaymentStatus() == PaymentStatus.REJECTED) {
            return;
        }

        String reason = "Erro";

        if (paymentIntent.getLastPaymentError() == null) {
            reason = paymentIntent.getLastPaymentError().getMessage();
            log.warn("Falha no pagamento: {} {}", payment.getId(), reason);
        }

        payment.setPaymentStatus(PaymentStatus.REJECTED);
        // TODO - Fazer campo de mensagem de erro
        //        payment.setFailureMessage(reason);

        paymentRepository.save(payment);
    }

    private void updatePaymentType(Payment payment, String type) {
        switch (type) {
            case "card" -> payment.setPaymentType(PaymentType.CARD);
            case "pix" -> payment.setPaymentType(PaymentType.PIX);
            case "boleto" -> payment.setPaymentType(PaymentType.BOLETO);
        }
    }

}
