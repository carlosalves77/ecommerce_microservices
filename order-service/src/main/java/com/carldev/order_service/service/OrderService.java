package com.carldev.order_service.service;

import com.carldev.order_service.dto.request.AddItemRequestDTO;
import com.carldev.order_service.dto.request.CheckoutCreate;
import com.carldev.order_service.dto.request.PaymentSuccessConsumer;
import com.carldev.order_service.dto.response.DeletePaymentResponseDTO;
import com.carldev.order_service.dto.response.OrderStatusResponseDTO;
import com.carldev.order_service.dto.response.OrdersResponseDTO;
import com.carldev.order_service.feignClient.ProductCatalogClient;
import com.carldev.order_service.kafka.producer.PaymentSuccessEvent;
import com.carldev.order_service.mapper.OrderMapper;
import com.carldev.order_service.model.Order;
import com.carldev.order_service.model.OrderItem;
import com.carldev.order_service.repository.OrderRepository;
import com.carldev.order_service.util.OrderStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ProductCatalogClient productCatalogClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper,
                        ApplicationEventPublisher eventPublisher,
                        ProductCatalogClient productCatalogClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.eventPublisher = eventPublisher;
        this.productCatalogClient = productCatalogClient;
    }

    @KafkaListener(topics = "payment-success", groupId = "payment-group")
    public void paymentConsumer(String messageJson) throws JsonProcessingException {

        PaymentSuccessConsumer paymentSuccessConsumer = objectMapper.readValue(
                messageJson, PaymentSuccessConsumer.class
        );


        Order orderPayment = orderRepository.findByOrderNumber(paymentSuccessConsumer.orderNumber())
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        if (orderPayment.getStatus().equals(OrderStatus.PAID)) {
            log.info("Ordem de pagamento consta como pago");
            return;
        }

        PaymentSuccessEvent paymentSuccess = new PaymentSuccessEvent(
                orderPayment.getUserId(),
                orderPayment.getUserName(),
                orderPayment.getEmail(),
                String.valueOf(orderPayment.getOrderNumber()),
                orderPayment.getTotalAmount().toString(),
                paymentSuccessConsumer.paymentMethod(),
                paymentSuccessConsumer.cardLast4(),
                orderPayment.getUpdateAt().toString(),
                paymentSuccessConsumer.items()
        );

        orderPayment.setStatus(OrderStatus.PAID);
        orderPayment.setUpdateAt(LocalDateTime.now());

        orderRepository.save(orderPayment);

        eventPublisher.publishEvent(paymentSuccess);

    }

    @KafkaListener(topics = "checkout-create", groupId = "order-service-group")
    public void createCheckout(String messageJson) throws JsonProcessingException {

        CheckoutCreate checkoutCreate = objectMapper.readValue(messageJson, CheckoutCreate.class);

        Order order = new Order();
        order.setUserId(checkoutCreate.userId());
        order.setTotalAmount(checkoutCreate.totalAmount());
        order.setUpdateAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setUserName(checkoutCreate.userName());
        order.setEmail(checkoutCreate.email());
        order.setOrderNumber(checkoutCreate.orderNumber());
        order.setCreatedAt(LocalDateTime.now());

        checkoutCreate.items().forEach(items ->
        {
            OrderItem orderItem = new OrderItem();
            orderItem.setSkuCode(items.sku());
            orderItem.setQuantity(items.quantity());
            orderItem.setPrice(items.unitPrice());

            productCatalogClient.getProductDebit(new AddItemRequestDTO(orderItem.getSkuCode(),
                    orderItem.getQuantity()));
            order.addItem(orderItem);
        });


        orderRepository.save(order);
    }

    @Transactional
    public OrderStatusResponseDTO updateOrderStatus(OrderStatus orderStatus, Long orderNumber) {

        Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(
                () -> new RuntimeException("Número de ordem não encontrado")
        );

        if (order.getStatus() == OrderStatus.CANCELLED) {
            log.info("Produto está cancelado");
        }

        order.setStatus(orderStatus);
        order.setUpdateAt(LocalDateTime.now());

        // TODO - Implementar no Kafka o status

        return orderMapper.toOrderStatusResponseDto(order);
    }

    @Transactional
    public Page<OrdersResponseDTO> getAllOrders(int pageNumber) {

        int page = pageNumber - 1;

        Pageable pageable = PageRequest.of(page, 10);

        Page<Order> ordersList = orderRepository.findAll(pageable);

        return ordersList.map(orderMapper::toDto);
    }

    @Transactional
    public DeletePaymentResponseDTO deletePaymentByOrderNumber(Long orderNumber) {

        Order order = orderRepository.deleteByOrderNumber(orderNumber).orElseThrow(
                () -> new RuntimeException("Order Number not found")
        );

        return orderMapper.deleteDto(order);
    }


}
