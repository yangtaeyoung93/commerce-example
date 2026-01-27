package com.example.orders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class OrderPricingCalculator {
    private static final BigDecimal DISCOUNT_THRESHOLD = new BigDecimal("100.00");
    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.10");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.07");

    public PricingResult calculate(List<OrderItem> items) {
        BigDecimal subtotal = items.stream()
            .map(OrderItem::lineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = BigDecimal.ZERO;
        if (subtotal.compareTo(DISCOUNT_THRESHOLD) >= 0) {
            discount = subtotal.multiply(DISCOUNT_RATE);
        }
        BigDecimal taxable = subtotal.subtract(discount);
        BigDecimal tax = taxable.multiply(TAX_RATE);
        BigDecimal total = taxable.add(tax);

        return new PricingResult(
            scale(subtotal),
            scale(discount),
            scale(tax),
            scale(total)
        );
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
