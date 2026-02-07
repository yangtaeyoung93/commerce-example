package com.example.payment;

public record PaymentRequest(
        String orderId,  // 주문 ID
        String memberId,     // 사용자 ID
        Integer amount, // 결제 금액
        String paymentMethod,   // 결제 수단 (CARD, BANK, ...)
        String cardNumber,  // 카드 번호
        String cardExpiry,  // 유효기간 (MM/YY)
        String cardCvc, // CVC
        String customerName,    // 구매자명
        String customerEmail    // 이메일
) {}
