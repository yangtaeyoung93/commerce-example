package com.example.infra.orders;

import com.example.orders.Order;
import com.example.orders.OrderRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<Long, Order> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public long nextId() {
        return sequence.getAndIncrement();
    }

    @Override
    public Order save(Order order) {
        store.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>(store.values());
        orders.sort(Comparator.comparing(Order::getCreatedAt).reversed());
        return orders;
    }

    public void clear() {
        store.clear();
        sequence.set(1);
    }
}

