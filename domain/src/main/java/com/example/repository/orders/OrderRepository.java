package com.example.repository.orders;

import com.example.orders.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> ,OrderRepositoryCustom{
}
