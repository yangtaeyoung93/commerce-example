package com.example.orders;

import com.example.global.ValidationError;

import java.util.List;

public class OrderValidationException extends RuntimeException {
    private final List<ValidationError> errors;

    public OrderValidationException(List<ValidationError> errors) {
        super("Order validation failed");
        this.errors = List.copyOf(errors);
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}

