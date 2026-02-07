package com.example.orders;

import java.time.LocalDateTime;

public record OrderSearchCondition
    (
            Long id,      // id pk
            String memberId,      // 회원 id
            OrderStatus orderStatus, // 주문 상태 (CREATED, PAID, SHIPPED, CANCELLED)
            Integer minPrice,       // 최소 금액
            Integer maxPrice,       // 최대 금액
            LocalDateTime startDate, // 시작일
            LocalDateTime endDate    // 종료일
    )
{
}
