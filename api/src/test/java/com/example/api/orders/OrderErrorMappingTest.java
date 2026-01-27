package com.example.api.orders;

import com.example.global.ValidationError;
import com.example.orders.OrderValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderErrorMappingTest {

    @Test
    void createOrder_mapsDomainExceptionToErrorResponse() {
        ApiExceptionHandler handler = new ApiExceptionHandler();
        OrderValidationException ex = new OrderValidationException(
            List.of(new ValidationError("customerName", "Customer name is required"))
        );

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("ORDER_VALIDATION_FAILED", response.getBody().code());
    }
}
