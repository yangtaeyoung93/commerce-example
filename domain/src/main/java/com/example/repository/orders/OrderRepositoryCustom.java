package com.example.repository.orders;

import com.example.orders.Order;
import com.example.orders.OrderSearchCondition;
import com.example.orders.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderRepositoryCustom {

    List<Order> search(OrderSearchCondition condition);

    Page<Order> searchEntity(OrderSearchCondition condition, Pageable pageable);
    List<OrderResponse> searchDTO(OrderSearchCondition condition);

}
