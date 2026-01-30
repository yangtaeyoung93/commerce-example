package com.example.api.orders;

import com.example.orders.CreateOrderRequest;
import com.example.orders.Order;
import com.example.orders.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(order));
    }

    private OrderResponse toResponse(Order order) {
        String memberId = order.getMember() == null || order.getMember().getId() == null
            ? null
            : order.getMember().getId().toString();
        List<OrderItemResponse> items = order.getOrderItems().stream()
            .map(orderItem -> new OrderItemResponse(
                orderItem.getItem().getName(),
                orderItem.getCount(),
                BigDecimal.valueOf(orderItem.getOrderPrice()),
                BigDecimal.valueOf(orderItem.getTotalPrice())
            ))
            .toList();
        BigDecimal totalAmount = BigDecimal.valueOf(order.getTotalPrice());
        Instant createdAt = order.getOrderDate() == null
            ? null
            : order.getOrderDate().atZone(ZoneId.systemDefault()).toInstant();
        return new OrderResponse(
            order.getId(),
            memberId,
            order.getStatus(),
            items,
            totalAmount,
            createdAt
        );
    }
}

