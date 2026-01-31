package com.example.orders;

import com.example.orders.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public record OrderResponse(
    long id,
    String memberName,
    OrderStatus status,
    List<OrderItemResponse> items,
    BigDecimal totalAmount,
    LocalDateTime createdAt
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getMember().getName(),
                order.getStatus(),
                order.getOrderItems().stream()
                        .map(OrderItemResponse::from)
                        .toList(),
                BigDecimal.valueOf(order.getTotalPrice()),
                order.getOrderDate()
        );
    }
}
