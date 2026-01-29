package com.example.global.exception;

import java.util.Map;

public class NotFoundResourceException extends RuntimeException {
    private final Map<String, Object> details;

    public NotFoundResourceException(String message, Map<String, Object> details) {
        super(message);
        this.details = details;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
