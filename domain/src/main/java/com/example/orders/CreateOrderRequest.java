package com.example.orders;

import java.util.List;

public record CreateOrderRequest(String customerName, List<OrderItemRequest> items) {
}
