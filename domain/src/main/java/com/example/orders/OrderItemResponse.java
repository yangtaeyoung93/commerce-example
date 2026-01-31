package com.example.orders;

import com.querydsl.core.annotations.QueryProjection;

public record OrderItemResponse(
        Long orderId,
        String name,
        int quantity,
        int orderPrice
) {

    @QueryProjection
    public OrderItemResponse{
    }

    public static OrderItemResponse from(OrderItem orderItem){
        return new OrderItemResponse(orderItem.getOrder().getId(),orderItem.getItem().getName(), orderItem.getCount(), orderItem.getOrderPrice());
    }
}
