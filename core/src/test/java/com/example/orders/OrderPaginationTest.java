package com.example.orders;

import org.junit.jupiter.api.Test;

import com.example.infra.orders.InMemoryOrderRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderPaginationTest {

    @Test
    void listOrders_returnsRequestedPageAndSize() {
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        Validator validator = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();
        OrderService service = new OrderService(repository, validator, new OrderPricingCalculator());

        CreateOrderRequest base = new CreateOrderRequest(
            "Lee",
            List.of(new OrderItemRequest("Pen", 1, new BigDecimal("5.00")))
        );
        service.createOrder(base);
        service.createOrder(base);
        Order third = service.createOrder(new CreateOrderRequest(
            "Park",
            List.of(new OrderItemRequest("Notebook", 1, new BigDecimal("12.00")))
        ));
        service.shipOrder(third.getId());

        PageResponse<Order> page = service.listOrders(OrderStatus.CREATED, null, 0, 2);

        assertEquals(2, page.content().size());
        assertEquals(1, page.totalPages());
        assertEquals(2, page.totalElements());
    }
}

