package com.example.orders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
public class Order {
    private final long id;
    private final String customerName;
    private final List<OrderItem> items;
    private final OrderStatus status;
    private final BigDecimal subtotal;
    private final BigDecimal discount;
    private final BigDecimal tax;
    private final BigDecimal total;
    private final Instant createdAt;

    public Order(
        long id,
        String customerName,
        List<OrderItem> items,
        OrderStatus status,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal tax,
        BigDecimal total,
        Instant createdAt
    ) {
        this.id = id;
        this.customerName = customerName;
        this.items = List.copyOf(items);
        this.status = status;
        this.subtotal = subtotal;
        this.discount = discount;
        this.tax = tax;
        this.total = total;
        this.createdAt = createdAt;
    }

    public Order withStatus(OrderStatus newStatus) {
        return new Order(
            id,
            customerName,
            items,
            newStatus,
            subtotal,
            discount,
            tax,
            total,
            createdAt
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return id == order.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

