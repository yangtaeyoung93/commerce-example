package com.example.api.orders;

import com.example.orders.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<PageResponse> getOrders(
            OrderSearchCondition condition,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        PageResponse orders = orderService.searchPage(condition, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

    private OrderResponse toResponse(Order order) {
        String memberId = order.getMember() == null || order.getMember().getId() == null
            ? null
            : order.getMember().getId().toString();
        List<OrderItemResponse> items = order.getOrderItems().stream()
            .map(orderItem -> new OrderItemResponse(
                order.getId(),
                orderItem.getItem().getName(),
                orderItem.getCount(),
                orderItem.getTotalPrice()
            ))
            .toList();
        BigDecimal totalAmount = BigDecimal.valueOf(order.getTotalPrice());

        return new OrderResponse(
            order.getId(),
            memberId,
            order.getStatus(),
            items,
            totalAmount,
            order.getOrderDate()
        );
    }
}

