package com.example.orders;

import org.junit.jupiter.api.Test;

import com.example.infra.orders.InMemoryOrderRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderValidationTest {

    @Test
    void createOrder_rejectsMissingRequiredFields() {
        Validator validator = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();
        OrderService service = new OrderService(
            new InMemoryOrderRepository(),
            validator,
            new OrderPricingCalculator()
        );
        CreateOrderRequest request = new CreateOrderRequest(
            " ",
            List.of(new OrderItemRequest("", 0, new BigDecimal("-1.00")))
        );

        OrderValidationException ex = assertThrows(
            OrderValidationException.class,
            () -> service.createOrder(request)
        );

        assertTrue(ex.getErrors().stream().anyMatch(err -> err.field().equals("customerName")));
        assertTrue(ex.getErrors().stream().anyMatch(err -> err.field().equals("items[0].name")));
        assertTrue(ex.getErrors().stream().anyMatch(err -> err.field().equals("items[0].quantity")));
        assertTrue(ex.getErrors().stream().anyMatch(err -> err.field().equals("items[0].unitPrice")));
    }
}
