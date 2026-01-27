package com.example.api.orders;

import com.example.orders.InvalidOrderStateException;
import com.example.orders.OrderNotFoundException;
import com.example.orders.OrderValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(OrderValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(OrderValidationException ex) {
        ErrorResponse response = new ErrorResponse(
            "ORDER_VALIDATION_FAILED",
            ex.getMessage(),
            ex.getErrors(),
            Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(OrderNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
            "ORDER_NOT_FOUND",
            ex.getMessage(),
            List.of(),
            Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<ErrorResponse> handleState(InvalidOrderStateException ex) {
        ErrorResponse response = new ErrorResponse(
            "ORDER_STATE_INVALID",
            ex.getMessage(),
            List.of(),
            Instant.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
