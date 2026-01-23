package com.carldev.payment_service.service;

import com.carldev.payment_service.dto.response.GetPaymentsResponseDTO;
import com.carldev.payment_service.kafka.eventsDTO.OrderConsumeEvent;
import com.carldev.payment_service.dto.request.AddItemRequestDTO;
import com.carldev.payment_service.dto.request.ItemDTO;
import com.carldev.payment_service.kafka.eventsDTO.PaymentCreateEvent;
import com.carldev.payment_service.entity.Payment;
import com.carldev.payment_service.entity.PaymentItem;
import com.carldev.payment_service.feignClient.ProductCatalogClient;
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
import java.util.*;

@Slf4j
@Service
@CrossOrigin
public class PaymentConsumerService {


    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final ProductCatalogClient productCatalogClient;
    private final PaymentMapper paymentMapper;
    Event event;

    @Value("${STRIPE_API_KEY}")
    private String stripeApiKey;

    @Value("${WEBHOOK_SECRET_KEY}")
    private String webhookSecretKey;


    public PaymentConsumerService(PaymentRepository paymentRepository,
                                  ApplicationEventPublisher eventPublisher, ObjectMapper objectMapper,
                                  ProductCatalogClient productCatalogClient, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
        this.productCatalogClient = productCatalogClient;
        this.paymentMapper = paymentMapper;
    }

    @PostConstruct
    public void createApiKey() {
        Stripe.apiKey = stripeApiKey;

    }

    @KafkaListener(
            topics = "checkout-create",
            groupId = "${spring.kafka.consumer.group-id}")
    public void createPayment(OrderConsumeEvent orderConsumeEvent) {

        Payment payment = new Payment();

        payment.setUserId(orderConsumeEvent.userId());
        payment.setAmount(orderConsumeEvent.totalAmount());
        payment.setEmail(orderConsumeEvent.email());
        payment.setUserName(orderConsumeEvent.userName());
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


    public void webhook(String payload, String sigHeader) throws StripeException {

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecretKey);

        } catch (SignatureVerificationException e) {
            log.info("Erro de assinatura {} ", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Erro no Webhook" + e.getMessage());
        }

        if (Objects.equals(event.getType(), "payment_intent.succeeded")) {

            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            PaymentIntent paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().orElse(null);

            assert paymentIntent != null;
            String chargeId = paymentIntent.getLatestCharge();
            String paymentMethodType = "";
            String last4 = "";


            if (chargeId != null) {
                try {
                    Charge charge = Charge.retrieve(chargeId);

                    if (charge.getPaymentMethodDetails()
                            != null && charge.getPaymentMethodDetails().getCard() != null) {

                        last4 = charge.getPaymentMethodDetails().getCard().getLast4();
                        paymentMethodType = charge.getPaymentMethodDetails().getType();
                    }
                } catch (StripeException e) {
                    log.error("Erro ao buscar charge {}", e.getMessage());
                }

                Map<String, String> getMetadata = paymentIntent.getMetadata();
                String currencyPaymentCountry = paymentIntent.getCurrency();

                Map<String, String> paymentToProvider = new HashMap<>();
                paymentToProvider.put("userId", getMetadata.get("userId"));
                paymentToProvider.put("userEmail", getMetadata.get("email"));
                paymentToProvider.put("userName", getMetadata.get("userName"));
                paymentToProvider.put("amountTotal", getMetadata.get("amount"));
                paymentToProvider.put("listItems", getMetadata.get("items"));
                paymentToProvider.put("last4", last4);
                paymentToProvider.put("currency", currencyPaymentCountry);
                paymentToProvider.put("paymentMethod", paymentMethodType);
                paymentToProvider.put("paidAt", String.valueOf(Instant.now()));

                List<ItemDTO> listaDeItens = new ArrayList<>();

                try {
                    listaDeItens = objectMapper.readValue(paymentToProvider.get("listItems"),
                            new TypeReference<List<ItemDTO>>() {
                            });
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                String id = getMetadata.get("id");
                Payment payment = paymentRepository.findById(UUID.fromString(id)).orElseThrow(
                        () -> new RuntimeException("Usuário não encontrado")
                );

                payment.setPaymentStatus(PaymentStatus.APPROVED);
                switch (paymentMethodType) {
                    case "card" -> {
                        payment.setPaymentType(PaymentType.CARD);
                    }
                    case "pix" -> {
                        payment.setPaymentType(PaymentType.PIX);
                    }
                    case "boleto" -> {
                        payment.setPaymentType(PaymentType.BOLETO);
                    }
                }

                listaDeItens.forEach(items -> {
                    AddItemRequestDTO itemsDTO = new AddItemRequestDTO(
                            items.sku(),
                            items.quantity()
                    );
                    productCatalogClient.getProductDebit(itemsDTO);
                });

                paymentRepository.save(payment);

                PaymentCreateEvent paymentCreateEvent = toMapDto(paymentToProvider, listaDeItens);

                PaymentSuccessEvent event = PaymentSuccessEvent.fromEntity(paymentCreateEvent);

                eventPublisher.publishEvent(event);

            }

        } else if ("payment_intent.payment_failed".equals(event.getType())) {

            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            PaymentIntent paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().orElse(null);

            assert paymentIntent != null;
            Map<String, String> getMetadata = paymentIntent.getMetadata();

            String id = getMetadata.get("id");
            Payment payment = paymentRepository.findById(UUID.fromString(id)).orElseThrow(
                    () -> new RuntimeException("Usuário não encontrado")
            );

            payment.setPaymentStatus(PaymentStatus.REJECTED);

        }

    }

    public PaymentCreateEvent toMapDto(Map<String, String> paymentToProvider, List<ItemDTO> listaDeItens) {

        return new PaymentCreateEvent(
                UUID.fromString(paymentToProvider.get("userId")),
                paymentToProvider.get("userEmail"),
                paymentToProvider.get("userName"),
                paymentToProvider.get("amountTotal"),
                paymentToProvider.get("currency"),
                paymentToProvider.get("paymentMethod"),
                paymentToProvider.get("last4"),
                paymentToProvider.get("paidAt"),
                listaDeItens
        );
    }
}
