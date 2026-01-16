package com.carldev.notification_service.service;

import com.carldev.notification_service.kafka.eventsDTO.CreateAccountValidationEvent;
import com.carldev.notification_service.kafka.eventsDTO.PaymentSuccessConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NotificationService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${MAILTRAP_KEY}")
    private String mailTrapkey;

    final MailtrapConfig config = new MailtrapConfig.Builder()
            .token(mailTrapkey)
            .build();


    final MailtrapClient client = MailtrapClientFactory.createMailtrapClient(config);


    @KafkaListener(topics = "payment-service", groupId = "notification-service-group")
    public void handlePaymentSuccess(String  messageJson) {

     try {
         PaymentSuccessConsumer paymentSuccessConsumer = objectMapper.readValue(messageJson,
                 PaymentSuccessConsumer.class);

         String msg = "Olá " + paymentSuccessConsumer.userName() + "! Seu pagamento de R$ " +
                 paymentSuccessConsumer.totalAmount() + " foi aprovado. Pedido: "
                 + paymentSuccessConsumer.orderId();

         final MailtrapMail mail = MailtrapMail.builder()
                 .from(new Address("hello@demomailtrap.co", "Mailtrap Test"))
                 .to(List.of(new Address(paymentSuccessConsumer.userEmail())))
                 .subject("Compra realizada")
                 .text(msg)
                 .category("Integration Test")
                 .build();

         try {
             System.out.println(client.send(mail));
         } catch (Exception e) {
             System.out.println("Exception" + e.getCause());
         }


     } catch (Exception err) {
         log.error("Exception do payment success {}", err.getMessage());
     }

    }

    @KafkaListener(topics = "auth-producer", groupId = "notification-auth-group")
    public void handleAuthAccountValidation(String mensagemJson) {
       try {
           CreateAccountValidationEvent createAccountValidationEvent = objectMapper.readValue(mensagemJson,
                   CreateAccountValidationEvent.class);

           String msg = "Olá " + createAccountValidationEvent.userName() + "! Segue seu link para " +
                   "confirmação" +
                   "do cadastro: " + createAccountValidationEvent.accountValidation();

           final MailtrapMail mail = MailtrapMail.builder()
                   .from(new Address("hello@demomailtrap.co", "Mailtrap Test"))
                   .to(List.of(new Address(createAccountValidationEvent.email())))
                   .subject("Confirmação de conta")
                   .text(msg)
                   .category("Integration Test")
                   .build();

           try {
               System.out.println(client.send(mail));
           } catch (Exception e) {
               System.out.println("Exception" + e.getCause());
           }


       } catch (Exception e) {
           log.error("Exception {}", e.getMessage());
       }
    }


}
