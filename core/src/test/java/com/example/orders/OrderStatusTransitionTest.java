package com.example.orders;

import org.junit.jupiter.api.Test;

import com.example.infra.orders.InMemoryOrderRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderStatusTransitionTest {

    @Test
    void cancelOrder_failsWhenAlreadyShipped() {
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        OrderService service = new OrderService(repository, new OrderValidator(), new OrderPricingCalculator());

        CreateOrderRequest request = new CreateOrderRequest(
            "Kim",
            List.of(new OrderItemRequest("Book", 1, new BigDecimal("25.00")))
        );
        Order order = service.createOrder(request);
        service.shipOrder(order.getId());

        assertThrows(InvalidOrderStateException.class, () -> service.cancelOrder(order.getId()));
    }
}

