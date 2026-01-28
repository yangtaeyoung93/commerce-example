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

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(order));
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
            order.getId(),
            null,
            null,
            List.of(),
            null,
            null
        );
    }
}

