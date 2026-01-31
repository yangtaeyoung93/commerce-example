package com.example.repository.orders.impl;

import com.example.orders.*;
import com.example.repository.orders.OrderRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.item.QItem.item;
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
                .join(order.member, member).fetchJoin()
                .where(
                        order.status.eq(condition.orderStatus()),
                        memberNameEq(condition.memberName()),
                        statusEq(condition.orderStatus()),
                        priceBetween(condition.minPrice(),condition.maxPrice())
                )
                .fetch();
    }

    @Override
    public Page<Order> searchEntity(OrderSearchCondition condition, Pageable pageable) {

        // 데이터 조회 쿼리
        List<Order> orders = queryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .where(
                        statusEq(condition.orderStatus()),
                        memberNameEq(condition.memberName()),
                        priceBetween(condition.minPrice(), condition.maxPrice())
                )
                .offset(pageable.getOffset()) // 페이지 시작 번호
                .limit(pageable.getPageSize()) // 페이지 사이즈
                .fetch();

        // 전체 개수 쿼리
        JPAQuery<Long> countQuery = queryFactory.select(order.count())
                .from(order)
                .where(
                        statusEq(condition.orderStatus()),
                        memberNameEq(condition.memberName()),
                        priceBetween(condition.minPrice(), condition.maxPrice())
                );

        return PageableExecutionUtils.getPage(orders,pageable,countQuery::fetchOne);
    }

    @Override
    public List<OrderResponse> searchDTO(OrderSearchCondition condition) {
        List<OrderSearchDTO> orders = queryFactory
                .select(new QOrderSearchDTO(
                        order.id,
                        order.status,
                        order.orderDate,
                        order.totalPrice,
                        member.id,
                        member.name
                ))
                .from(order)
                .join(order.member, member)
                .where(
                        statusEq(condition.orderStatus()),
                        memberNameEq(condition.memberName()),
                        priceBetween(condition.minPrice(), condition.maxPrice())
                )
                .fetch();

        if (orders.isEmpty()) {
            return List.of();
        }

        List<Long> orderIds = orders.stream().map(OrderSearchDTO::getOrderId).toList();

        List<OrderItemResponse> itemDetails = queryFactory
                .select(new QOrderItemResponse(orderItem.order.id, item.name, orderItem.count, orderItem.orderPrice))
                .from(orderItem)
                .join(orderItem.item, item)
                .where(orderItem.order.id.in(orderIds))
                .fetch();

        Map<Long, List<OrderItemResponse>> itemMap = itemDetails.stream()
                .collect(Collectors.groupingBy(OrderItemResponse::orderId));

        return orders.stream()
                .map(o -> new OrderResponse(
                        o.getOrderId(),
                        o.getMemberName(),
                        o.getOrderStatus(),
                        itemMap.getOrDefault(o.getOrderId(), List.of()),
                        BigDecimal.valueOf(o.getTotalPrice()),
                        o.getOrderDate()
                ))
                .toList();

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
