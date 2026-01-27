package com.example.orders;

import java.math.BigDecimal;

public record PricingResult(BigDecimal subtotal, BigDecimal discount, BigDecimal tax, BigDecimal total) {
}
