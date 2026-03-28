package com.carldev.payment_service.service;

import com.carldev.payment_service.dto.response.GetPaymentsResponseDTO;
import com.carldev.payment_service.entity.Payment;
import com.carldev.payment_service.entity.PaymentItem;
import com.carldev.payment_service.mapper.PaymentMapper;
import com.carldev.payment_service.repository.PaymentRepository;
import com.carldev.payment_service.utils.PaymentStatus;
import com.carldev.payment_service.utils.PaymentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentConsumerServiceTest {

    @InjectMocks
    private PaymentConsumerService paymentConsumerService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PaymentMapper paymentMapper;


    @Test
    @DisplayName("Deve retornar página de pagamentos")
    void shouldGetPaymentsPaginated() {

        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(150.00));
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setEmail("test@test.com");
        payment.setUserName("Carlos");
        payment.setOrderNumber(123L);

        GetPaymentsResponseDTO dto = new GetPaymentsResponseDTO(
                payment.getId().toString(), BigDecimal.valueOf(150.00),
                PaymentStatus.PENDING, Instant.now(), null
        );

        Page<Payment> page = new PageImpl<>(List.of(payment));
        when(paymentRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(paymentMapper.toDto(payment)).thenReturn(dto);

        Page<GetPaymentsResponseDTO> result = paymentConsumerService.getPayment(1);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(PaymentStatus.PENDING, result.getContent().get(0).paymentStatus());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não há pagamentos")
    void shouldReturnEmptyPageWhenNoPayments() {

        Page<Payment> emptyPage = new PageImpl<>(List.of());
        when(paymentRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<GetPaymentsResponseDTO> result = paymentConsumerService.getPayment(1);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }



    @Test
    @DisplayName("Deve deletar pagamento pelo ID")
    void shouldDeletePaymentById() {

        UUID paymentId = UUID.randomUUID();

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setUserId(UUID.randomUUID());
        payment.setEmail("test@test.com");
        payment.setUserName("Carlos");
        payment.setAmount(BigDecimal.TEN);
        payment.setOrderNumber(1L);
        payment.setPaymentStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        paymentConsumerService.deleteById(paymentId.toString());

        verify(paymentRepository, times(1)).deleteById(paymentId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagamento não encontrado ao deletar")
    void shouldThrowWhenPaymentNotFoundOnDelete() {

        UUID paymentId = UUID.randomUUID();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> paymentConsumerService.deleteById(paymentId.toString()));

        verify(paymentRepository, never()).deleteById(any());
    }



    @Test
    @DisplayName("Deve deletar todos os pagamentos")
    void shouldDeleteAllPayments() {

        paymentConsumerService.deleteAllPayments();

        verify(paymentRepository, times(1)).deleteAll();
    }
}
