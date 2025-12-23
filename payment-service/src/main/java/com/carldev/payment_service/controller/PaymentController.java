package com.carldev.payment_service.controller;

import com.carldev.payment_service.dto.request.GetItemsRequestDTO;
import com.carldev.payment_service.dto.response.GetPaymentsResponseDTO;
import com.carldev.payment_service.service.PaymentConsumerService;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/payment")
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

        return new ResponseEntity<>("Evento craido", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<GetPaymentsResponseDTO>> getAllPayments(
            @RequestParam(value = "userName", required = false) String name
            ) {
        GetItemsRequestDTO getItemsRequestDTO = new GetItemsRequestDTO(
                name
        );

        List<GetPaymentsResponseDTO> paymentList = paymentConsumerService.getPayment(getItemsRequestDTO);

        return ResponseEntity.ok().body(paymentList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(
            @PathVariable String id
    ) {
        paymentConsumerService.deleteById(id);

        return ResponseEntity.ok().body("Pagamento deletado");
    }

    @PostMapping("{id}/{customerId}")
    public ResponseEntity<String> handlePayment(
            @PathVariable("id") UUID id,
            @PathVariable("customerId") String customerId

    ) {
        paymentConsumerService.handlePayment(id, customerId);

        return ResponseEntity.ok().body("Pagamento concluido");
    }

}
