package com.example.repository.orders.impl;

import com.example.orders.Order;
import com.example.orders.OrderSearchCondition;
import com.example.orders.OrderStatus;
import com.example.orders.QOrder;
import com.example.repository.orders.OrderRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.member.QMember.member;
import static com.example.orders.QOrder.order;
import static com.example.orders.QOrderItem.orderItem;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Order> search(OrderSearchCondition condition) {


        return queryFactory
                .selectFrom(order)
                .join(order.orderItems, orderItem).fetchJoin()
                .join(order.member, member).fetchJoin()
                .where(
                        order.status.eq(condition.orderStatus()),
                        memberNameEq(condition.memberName()),
                        statusEq(condition.orderStatus()),
                        priceBetween(condition.minPrice(),condition.maxPrice())
                )
                .fetch();
    }

    private BooleanExpression memberNameEq(String name) {
        return hasText(name) ? member.name.contains(name) : null;
    }


    private BooleanExpression statusEq(OrderStatus status) {
        return status != null ? order.status.eq(status) : null;
    }

    private BooleanExpression priceBetween(Integer min, Integer max) {
        if (min == null || max == null) return null;
        return order.totalPrice.between(min, max);
    }
}
