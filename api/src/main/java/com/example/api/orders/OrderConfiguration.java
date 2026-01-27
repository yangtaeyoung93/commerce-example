package com.example.api.orders;

import com.example.orders.OrderPricingCalculator;
import com.example.orders.OrderRepository;
import com.example.orders.OrderService;
import com.example.orders.OrderValidator;

import com.example.infra.orders.InMemoryOrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfiguration {

    @Bean
    public OrderRepository orderRepository() {
        return new InMemoryOrderRepository();
    }

    @Bean
    public OrderValidator orderValidator() {
        return new OrderValidator();
    }

    @Bean
    public OrderPricingCalculator orderPricingCalculator() {
        return new OrderPricingCalculator();
    }

    @Bean
    public OrderService orderService(
        OrderRepository repository,
        OrderValidator validator,
        OrderPricingCalculator pricingCalculator
    ) {
        return new OrderService(repository, validator, pricingCalculator);
    }
}
