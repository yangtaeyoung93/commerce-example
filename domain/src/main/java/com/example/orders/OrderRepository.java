package com.example.orders;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    long nextId();

    Order save(Order order);

    Optional<Order> findById(long id);

    List<Order> findAll();
}

