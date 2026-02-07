package com.example.payment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderId;  // 주문 ID

    private String paymentId;  // PG사 결제 ID

    @Column(nullable = false)
    private Integer amount;  // 결제 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    public Payment(String orderId, Integer amount) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("주문 ID 필수");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야 함");
        }

        this.orderId = orderId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    // 완료
    public void complete(String paymentId) {
        if (this.status == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("이미 완료된 결제");
        }

        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("결제 ID 필수");
        }

        this.paymentId = paymentId;
        this.status = PaymentStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    // 실패
    public void fail(String reason) {
        if (this.status == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("완료된 결제는 실패 처리 불가");
        }

        this.status = PaymentStatus.FAILED;
    }

    // 조회용
    public boolean isCompleted() {
        return this.status == PaymentStatus.COMPLETED;
    }
}
