package com.example.api.orders;

import com.example.orders.CreateOrderRequest;
import com.example.orders.Order;
import com.example.orders.OrderService;
import com.example.orders.OrderStatus;
import com.example.orders.PageResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
        Order order = service.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(order));
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable("id") long id) {
        return toResponse(service.getOrder(id));
    }

    @GetMapping
    public PageResponse<OrderResponse> list(
        @RequestParam(name = "status", required = false) OrderStatus status,
        @RequestParam(name = "q", required = false) String query,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<Order> pageResult = service.listOrders(status, query, page, size);
        List<OrderResponse> content = pageResult.content().stream()
            .map(this::toResponse)
            .toList();
        return new PageResponse<>(content, pageResult.page(), pageResult.size(), pageResult.totalElements(),
            pageResult.totalPages());
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
            .map(item -> new OrderItemResponse(
                item.getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.lineTotal()
            ))
            .toList();
        return new OrderResponse(
            order.getId(),
            order.getCustomerName(),
            order.getStatus(),
            items,
            order.getSubtotal(),
            order.getDiscount(),
            order.getTax(),
            order.getTotal(),
            order.getCreatedAt()
        );
    }
}

