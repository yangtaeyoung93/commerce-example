package com.example.orders;

import java.math.BigDecimal;

public record OrderItemRequest(String name, int quantity, BigDecimal unitPrice) {
}
