package com.example.api.orders;

import com.example.orders.OrderPricingCalculator;
import com.example.orders.OrderRepository;
import com.example.orders.OrderService;

import com.example.infra.orders.InMemoryOrderRepository;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfiguration {

    @Bean
    public OrderRepository orderRepository() {
        return new InMemoryOrderRepository();
    }

    @Bean
    public OrderPricingCalculator orderPricingCalculator() {
        return new OrderPricingCalculator();
    }

    @Bean
    public OrderService orderService(
        OrderRepository repository,
        Validator validator,
        OrderPricingCalculator pricingCalculator
    ) {
        return new OrderService(repository, validator, pricingCalculator);
    }
}
