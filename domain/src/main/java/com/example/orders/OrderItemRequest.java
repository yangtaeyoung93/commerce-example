package com.example.orders;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderItemRequest(
    @NotBlank String name,
    @Positive int quantity,
    @NotNull @DecimalMin(value = "0.0") BigDecimal unitPrice
) {
}
