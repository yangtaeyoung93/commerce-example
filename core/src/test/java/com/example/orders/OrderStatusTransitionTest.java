package com.example.orders;

import org.junit.jupiter.api.Test;

import com.example.infra.orders.InMemoryOrderRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderStatusTransitionTest {

    @Test
    void cancelOrder_failsWhenAlreadyShipped() {
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        Validator validator = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();
        OrderService service = new OrderService(repository, validator, new OrderPricingCalculator());

        CreateOrderRequest request = new CreateOrderRequest(
            "Kim",
            List.of(new OrderItemRequest("Book", 1, new BigDecimal("25.00")))
        );
        Order order = service.createOrder(request);
        service.shipOrder(order.getId());

        assertThrows(InvalidOrderStateException.class, () -> service.cancelOrder(order.getId()));
    }
}

