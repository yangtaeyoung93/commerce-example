package com.example.api.orders;

import com.example.orders.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
    long id,
    String customerName,
    OrderStatus status,
    List<OrderItemResponse> items,
    BigDecimal subtotal,
    BigDecimal discount,
    BigDecimal tax,
    BigDecimal total,
    Instant createdAt
) {
}
