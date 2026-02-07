package com.example.payment;

import java.time.LocalDateTime;

public record PaymentResponse(
        PaymentStatus status,
        String paymentId,
        String message,
        LocalDateTime paidAt
) {

    public static PaymentResponse success(String paymentId) {
        return new PaymentResponse(
                PaymentStatus.COMPLETED,
                paymentId,
                "결제 완료",
                LocalDateTime.now()
        );
    }

    public static PaymentResponse failed(String message) {
        return new PaymentResponse(
                PaymentStatus.FAILED,
                null,
                message,
                null
        );
    }

    public static PaymentResponse alreadyProcessed(String paymentId) {
        return new PaymentResponse(
                PaymentStatus.COMPLETED,
                paymentId,
                "이미 처리된 결제",
                null
        );
    }
}
