package com.carldev.payment_service.controller;

import com.carldev.payment_service.dto.response.GetPaymentsResponseDTO;
import com.carldev.payment_service.dto.response.OrderItemResponseDTO;
import com.carldev.payment_service.service.PaymentConsumerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentConsumerService paymentConsumerService;

    public PaymentController(PaymentConsumerService paymentConsumerService) {
        this.paymentConsumerService = paymentConsumerService;
    }

    @PostMapping("/webhook/stripe")
    public ResponseEntity<String> createPayment(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) throws StripeException {

        paymentConsumerService.webhook(payload, sigHeader);

        return  ResponseEntity.status(HttpStatus.CREATED).body("Evento criado com sucesso");
    }

    @GetMapping
    public ResponseEntity<Page<GetPaymentsResponseDTO>> getAllPayments(
            @RequestParam("page") int page
    ) {

        Page<GetPaymentsResponseDTO> paymentList = paymentConsumerService.getPayment(page);

        return ResponseEntity.status(HttpStatus.OK).body(paymentList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(
            @PathVariable String id
    ) {
        paymentConsumerService.deleteById(id);

        return ResponseEntity.status(HttpStatus.OK).body("Pagamento deletado");
    }

    @PostMapping("{id}/{customerId}")
    public ResponseEntity<List<OrderItemResponseDTO>> handlePayment(
            @PathVariable("id") UUID id,
            @PathVariable("customerId") String customerId

    ) throws JsonProcessingException {

      List<OrderItemResponseDTO> payment =  paymentConsumerService.handlePayment(id, customerId);

        return ResponseEntity.status(HttpStatus.OK).body(payment);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllPayment() {
        paymentConsumerService.deleteAllPayments();
        return ResponseEntity.status(HttpStatus.OK).body("Todos os pagamentos deletados");
    }

}
