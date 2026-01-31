package com.example.orders;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class OrderSearchDTO{
    Long orderId;
    OrderStatus orderStatus;
    LocalDateTime orderDate;
    int totalPrice;
    Long memberId;
    String memberName;

    @QueryProjection
    public OrderSearchDTO(Long orderId, OrderStatus orderStatus, LocalDateTime orderDate, int totalPrice, Long memberId, String memberName) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.memberId = memberId;
        this.memberName = memberName;
    }


}
