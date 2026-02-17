package com.carldev.order_service.controller;

import com.carldev.order_service.dto.request.OrderStatusRequestDTO;
import com.carldev.order_service.dto.response.DeletePaymentResponseDTO;
import com.carldev.order_service.dto.response.OrderStatusResponseDTO;
import com.carldev.order_service.dto.response.OrdersResponseDTO;
import com.carldev.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<Page<OrdersResponseDTO>> getOrdersList(
            @RequestParam(value = "page", required = false) int page
    ) {

        Page<OrdersResponseDTO> listPages = orderService.getAllOrders(page);

        return ResponseEntity.ok().body(listPages);
    }

    @DeleteMapping("/{orderNumber}")
    public ResponseEntity<DeletePaymentResponseDTO> deleteByOrderNumber(
            @PathVariable Long orderNumber
    ) {

        DeletePaymentResponseDTO deletePayment = orderService.deletePaymentByOrderNumber(orderNumber);

        return ResponseEntity.ok().body(deletePayment);
    }

    @PatchMapping("/{orderNumber}")
    public ResponseEntity<OrderStatusResponseDTO> updateStatusOrder(
            @PathVariable long orderNumber,
            @Valid @RequestBody OrderStatusRequestDTO body
    ) {

        OrderStatusResponseDTO responseDTO = orderService.updateOrderStatus(body.status(), orderNumber);

        return ResponseEntity.ok().body(responseDTO);
    }
}
