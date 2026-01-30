package com.example.repository.orders;

import com.example.orders.Order;
import com.example.orders.OrderSearchCondition;

import java.util.List;

public interface OrderRepositoryCustom {

    List<Order> search(OrderSearchCondition condition);
}
