package com.example.orders;

import com.example.global.ValidationError;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderValidator {

    public void validate(CreateOrderRequest request) {
        List<ValidationError> errors = new ArrayList<>();
        if (request == null) {
            errors.add(new ValidationError("order", "Order payload is required"));
            throw new OrderValidationException(errors);
        }

        if (request.customerName() == null || request.customerName().isBlank()) {
            errors.add(new ValidationError("customerName", "Customer name is required"));
        }

        if (request.items() == null || request.items().isEmpty()) {
            errors.add(new ValidationError("items", "At least one item is required"));
        } else {
            for (int i = 0; i < request.items().size(); i++) {
                OrderItemRequest item = request.items().get(i);
                String prefix = "items[" + i + "]";
                if (item == null) {
                    errors.add(new ValidationError(prefix, "Item is required"));
                    continue;
                }
                if (item.name() == null || item.name().isBlank()) {
                    errors.add(new ValidationError(prefix + ".name", "Item name is required"));
                }
                if (item.quantity() <= 0) {
                    errors.add(new ValidationError(prefix + ".quantity", "Quantity must be greater than 0"));
                }
                BigDecimal price = item.unitPrice();
                if (price == null) {
                    errors.add(new ValidationError(prefix + ".unitPrice", "Unit price is required"));
                } else if (price.compareTo(BigDecimal.ZERO) < 0) {
                    errors.add(new ValidationError(prefix + ".unitPrice", "Unit price cannot be negative"));
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new OrderValidationException(errors);
        }
    }
}
