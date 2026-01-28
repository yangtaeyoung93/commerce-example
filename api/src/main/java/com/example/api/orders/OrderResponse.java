package com.example.api.orders;

import com.example.orders.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
    long id,
    String userId,
    OrderStatus status,
    List<OrderItemResponse> items,
    BigDecimal totalAmount,
    Instant createdAt
) {
}
