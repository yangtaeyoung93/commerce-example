package com.example.orders;

import com.example.item.Item;
import com.example.member.Member;
import com.example.member.exception.MemberNotFoundException;
import com.example.repository.item.ItemRepository;
import com.example.repository.member.MemberRepository;
import com.example.repository.orders.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    public Order createOrder(CreateOrderRequest request) {
        Long memberId = request.memberId();

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

    public List<Order> search(OrderSearchCondition condition){
        List<Order> search = orderRepository.search(condition);

        return search;
    }
}