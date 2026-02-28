package com.carldev.notification_service.service;

import com.carldev.notification_service.dto.CreateAccountValidationEvent;
import com.carldev.notification_service.dto.OrderChangeNotification;
import com.carldev.notification_service.dto.PaymentSuccessConsumer;
import com.carldev.notification_service.dto.ResetPasswordConsumer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class NotificationService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TemplateEngine templateEngine;


    String token = System.getenv("MAILTRAP_KEY");


    final MailtrapConfig config = new MailtrapConfig.Builder()
            .token(token)
            .build();

    final MailtrapClient client = MailtrapClientFactory.createMailtrapClient(config);

    public NotificationService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }


    @KafkaListener(topics = "payment-success-notification", groupId = "notification-service-group")
    public void handlePaymentSuccess(String messageJson) {

        try {
            PaymentSuccessConsumer paymentSuccessConsumer = objectMapper.readValue(messageJson,
                    PaymentSuccessConsumer.class);

            Context context = new Context();
            context.setVariable("userName", paymentSuccessConsumer.userName());
            context.setVariable("orderNumber", paymentSuccessConsumer.orderNumber());
            context.setVariable("totalAmount", paymentSuccessConsumer.totalAmount());
            context.setVariable("currency", paymentSuccessConsumer.currency().toUpperCase());
            context.setVariable("paymentMethod", paymentSuccessConsumer.paymentMethod());
            context.setVariable("cardLast4", paymentSuccessConsumer.cardLast4());
            context.setVariable("paidAt", paymentSuccessConsumer.paidAt());
            context.setVariable("address", paymentSuccessConsumer.addressPaymentSuccess());
            context.setVariable("items", paymentSuccessConsumer.items());

            String htmlBody = templateEngine.process("email/payment-success", context);

            final MailtrapMail mail = MailtrapMail.builder()
                    .from(new Address("hello@carldev.online", "CarlDev Shop"))
                    .to(List.of(new Address(paymentSuccessConsumer.userEmail())))
                    .subject("Pagamento Aprovado: Pedido #" + paymentSuccessConsumer.orderNumber())
                    .html(htmlBody)
                    .text("Pagamento aprovado para o pedido #" + paymentSuccessConsumer.orderNumber())
                    .category("Payment Success")
                    .build();
            try {
                client.send(mail);
            } catch (Exception e) {
                System.out.println("Exception ao tentar enviar e-mail de payment" + e.getMessage());
            }


        } catch (Exception err) {
            log.error("Exception do payment success {}", err.getMessage());
        }

    }

    @KafkaListener(topics = "auth-producer", groupId = "notification-auth-group")
    public void handleAuthAccountValidation(String messageJson) {
        try {
            CreateAccountValidationEvent createAccountValidationEvent = objectMapper.readValue(messageJson,
                    CreateAccountValidationEvent.class);

            String msg = "Olá " + createAccountValidationEvent.userName() + "! Segue seu link para " +
                    "confirmação" +
                    "do cadastro: " + createAccountValidationEvent.accountValidation();

            final MailtrapMail mail = MailtrapMail.builder()
                    .from(new Address("hello@carldev.online", "Mailtrap Test"))
                    .to(List.of(new Address(createAccountValidationEvent.email())))
                    .subject("Confirmação de conta")
                    .text(msg)
                    .category("Integration Test")
                    .build();

            try {
                client.send(mail);
            } catch (Exception e) {
                log.error("Exception ao tentar enviar e-mail do pagamento{}", e.getMessage());
            }


        } catch (Exception e) {
            log.error("Exception try catch pagamento {}", e.getMessage());
        }
    }


    @KafkaListener(topics = "order-change-notification", groupId = "notification-service-group")
    public void handleOrderStatusChanges(String messageJson) throws JsonProcessingException {

        OrderChangeNotification orderChangeNotification = objectMapper.readValue(messageJson,
                OrderChangeNotification.class);
        Context context = new Context();
        context.setVariable("userName", orderChangeNotification.userName());
        context.setVariable("orderNumber", orderChangeNotification.orderNumber());
        context.setVariable("status", orderChangeNotification.status());
        context.setVariable("totalAmount", orderChangeNotification.totalAmount());
        context.setVariable("updateAt", orderChangeNotification.updateAt());
        context.setVariable("orderId", orderChangeNotification.orderId());

        String htmlBody = templateEngine.process("email/order-status", context);

        try {
            MailtrapMail mail = MailtrapMail.builder()
                    .from(new Address("hello@carldev.online", "CarlDev Shop"))
                    .to(List.of(new Address(orderChangeNotification.userEmail())))
                    .subject("Atualização do Pedido #" + orderChangeNotification.orderNumber())
                    .html(htmlBody)
                    .text("Seu pedido #" + orderChangeNotification.orderNumber() + " foi atualizado para: "
                            + orderChangeNotification.status())
                    .category("Order Notification")
                    .build();

            try {
                client.send(mail);
            } catch (Exception e) {
                log.error("Exception ao tentar enviar e-mail de order status{}", e.getMessage());

            }

        } catch (Exception e) {
            log.error("Exception try catch na alteração do pagamento {}", e.getMessage());
        }


    }

    @KafkaListener(topics = "auth-reset-password", groupId = "notification-auth-group")
    public void handleResetPasswordToken(String messageJson) throws JsonProcessingException {

        ResetPasswordConsumer resetPasswordConsumer = objectMapper.readValue(messageJson,
                ResetPasswordConsumer.class);
        Context context = new Context();
        context.setVariable("passwordToken", resetPasswordConsumer.passwordToken());
        context.setVariable("username", resetPasswordConsumer.username());


        String htmlBody = templateEngine.process("email/reset-password", context);

        try {
            MailtrapMail mail = MailtrapMail.builder()
                    .from(new Address("hello@carldev.online", "CarlDev Shop"))
                    .to(List.of(new Address(resetPasswordConsumer.userEmail())))
                    .subject("Redefinição de senha")
                    .html(htmlBody)
                    .text("Olá " + resetPasswordConsumer.username() + ". Seu token de redefinição é: "
                    + resetPasswordConsumer.passwordToken())
                    .category("Password Reset")
                    .build();

            try {
                client.send(mail);
            } catch (Exception e) {
                log.error("Exception ao tentar enviar e-mail de order status{}", e.getMessage());

            }

        } catch (Exception e) {
            log.error("Exception try catch na alteração do pagamento {}", e.getMessage());
        }


    }


}
