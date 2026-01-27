package com.example.api.orders;

import com.example.global.ValidationError;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
    String code,
    String message,
    List<ValidationError> details,
    Instant timestamp
) {
}
