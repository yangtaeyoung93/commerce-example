package com.example.orders;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderPricingCalculatorTest {

    @Test
    void calculateTotal_appliesDiscountAndTax() {
        OrderPricingCalculator calculator = new OrderPricingCalculator();
        List<OrderItem> items = List.of(
            new OrderItem("Shoes", 2, new BigDecimal("30.00")),
            new OrderItem("Hat", 1, new BigDecimal("50.00"))
        );

        PricingResult result = calculator.calculate(items);

        assertEquals(new BigDecimal("110.00"), result.subtotal());
        assertEquals(new BigDecimal("11.00"), result.discount());
        assertEquals(new BigDecimal("6.93"), result.tax());
        assertEquals(new BigDecimal("105.93"), result.total());
    }
}

