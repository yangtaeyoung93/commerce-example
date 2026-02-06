package com.example.orders;

import com.example.infra.redis.CacheService;
import com.example.item.Item;
import com.example.member.Member;
import com.example.member.exception.MemberNotFoundException;
import com.example.repository.item.ItemRepository;
import com.example.repository.member.MemberRepository;
import com.example.repository.orders.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final CacheService cacheService;


    /**
     * @CacheEvict: 캐시 무효화
     */
//    @CacheEvict(
//            value = "orders",
//            key = "'order:' + #orderId"
//    )
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Long memberId = request.memberId();

        cacheService.evictUserCondition(memberId);

        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        List<OrderItem> orderItems = request.items().stream()
            .map(itemRequest -> {
                Item item = itemRepository.findById(itemRequest.itemId())
                    .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemRequest.itemId()));
                return OrderItem.createOrderItem(item, item.getPrice(), itemRequest.quantity());
            })
            .toList();

        Order order = Order.createOrder(member, orderItems);
        order.getTotalPrice();
        return orderRepository.save(order);
    }

    /**
     * 항상 메서드 실행 후 캐시 갱신 (Write-Through)
     */
    @Transactional
    @CachePut(
            value = "orders",
            key = "'member:' + #condition.memberId + ':page:' + #pageable.pageNumber",
            unless = "#result == null"
    )
    public PageResponse updateOrder(Long orderId, CreateOrderRequest request, OrderSearchCondition condition, Pageable pageable) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // 기존 주문 항목 재고 복원
        order.getOrderItems().forEach(OrderItem::cancel);
        order.getOrderItems().clear();

        // 새 주문 항목 추가
        List<OrderItem> newOrderItems = request.items().stream()
                .map(itemRequest -> {
                    Item item = itemRepository.findById(itemRequest.itemId())
                            .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemRequest.itemId()));
                    return OrderItem.createOrderItem(item, item.getPrice(), itemRequest.quantity());
                })
                .toList();

        newOrderItems.forEach(order::addOrderItem);

        // 캐시 갱신을 위해 searchPage와 동일한 PageResponse 반환
        Page<OrderResponse> orders = orderRepository.searchEntity(condition, pageable)
                .map(OrderResponse::from);

        return new PageResponse(
                orders.getContent(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                orders.getTotalElements(),
                orders.getTotalPages()
        );
    }

    public List<OrderResponse> search(OrderSearchCondition condition){
        List<Order> orders = orderRepository.search(condition);

        List<OrderResponse> result = orders.stream()
                .map(o -> new OrderResponse(
                        o.getId(),
                        o.getMember().getName(),
                        o.getStatus(),
                        o.getOrderItems().stream()
                                .map(oi -> new OrderItemResponse(oi.getOrder().getId(),oi.getItem().getName(),oi.getCount(),oi.getTotalPrice()))
                                .toList(),
                        BigDecimal.valueOf(o.getTotalPrice()),
                        o.getOrderDate()

                ))
                .toList();

        return result;
    }

    /**
     * 캐시 조회 → 미스 시 메서드 실행 → 결과 저장
     * 페이지별로 필요한 것만 저장(10개)
     * 네트워크 효율적
     * 메모리 절약
     */
    @Cacheable(
            value = "orders",  // 캐시 이름
            key = "'member:' + #condition.memberId + ':page:' + #pageable.pageNumber",
            unless = "#result == null"  // null은 캐싱 안 함
    )
    public PageResponse searchPage(OrderSearchCondition condition, Pageable pageable){
        Page<OrderResponse> orders = orderRepository.searchEntity(condition, pageable)
                .map(OrderResponse::from);

        PageResponse pageResponse = new PageResponse(
                orders.getContent(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                orders.getTotalElements(),
                orders.getTotalPages()
                );
        return pageResponse;
    }


    /**
     * @Caching: 여러 캐시 작업 조합
     */
//    @Caching(
//            evict = {
//                    @CacheEvict(value = "orders", key = "'order:' + #orderId"),
//                    @CacheEvict(value = "orders", key = "'user:' + #userId + ':orders'")
//            }
//    )
//    public void updateOrderStatus(String orderId, String userId, OrderStatus status) {
//        // 주문 상태 변경 시 관련 캐시 모두 무효화
//        orderRepository.updateStatus(orderId, status);
//    }

}
