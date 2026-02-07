package com.example.orders;

import com.example.item.Item;
import com.example.member.Member;
import com.example.repository.item.ItemRepository;
import com.example.repository.member.MemberRepository;
import com.example.repository.orders.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_savesOrderWithItems() {
        Long memberId = 1L;
        Long itemId = 10L;
        int quantity = 2;

        Address address = new Address("Seoul", "마포구", "69050");
        Member member = new Member("Tester","테스트계정",address);
        Item item = new Item("Test Item", 1500, 10);
        item.setId(itemId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(
            memberId,
            List.of(new OrderItemRequest(itemId, quantity))
        );

        Order result = orderService.createOrder(request);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertSame(member, savedOrder.getMember());
        assertEquals(1, savedOrder.getOrderItems().size());
        assertEquals(OrderStatus.CREATED, savedOrder.getStatus());
        assertNotNull(savedOrder.getOrderDate());
        assertSame(item, savedOrder.getOrderItems().get(0).getItem());
        assertEquals(quantity, savedOrder.getOrderItems().get(0).getCount());
        assertSame(savedOrder, result);
    }
}
