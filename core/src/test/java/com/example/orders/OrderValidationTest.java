package com.example.orders;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderValidationTest {

    @Test
    void createOrder_rejectsMissingRequiredFields() {
        OrderValidator validator = new OrderValidator();
        CreateOrderRequest request = new CreateOrderRequest(
            " ",
            List.of(new OrderItemRequest("", 0, new BigDecimal("-1.00")))
        );

        OrderValidationException ex = assertThrows(
            OrderValidationException.class,
            () -> validator.validate(request)
        );

        assertTrue(ex.getErrors().stream().anyMatch(err -> err.field().equals("customerName")));
        assertTrue(ex.getErrors().stream().anyMatch(err -> err.field().equals("items[0].name")));
        assertTrue(ex.getErrors().stream().anyMatch(err -> err.field().equals("items[0].quantity")));
        assertTrue(ex.getErrors().stream().anyMatch(err -> err.field().equals("items[0].unitPrice")));
    }
}

