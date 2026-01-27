package com.example.orders;

import com.example.global.ValidationError;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderService {
    private final OrderRepository repository;
    private final Validator validator;
    private final OrderPricingCalculator pricingCalculator;

    public OrderService(OrderRepository repository, Validator validator, OrderPricingCalculator pricingCalculator) {
        this.repository = repository;
        this.validator = validator;
        this.pricingCalculator = pricingCalculator;
    }

    public Order createOrder(CreateOrderRequest request) {
        validateRequest(request);
        List<OrderItem> items = request.items().stream()
            .map(item -> new OrderItem(item.name(), item.quantity(), item.unitPrice()))
            .toList();

        PricingResult pricing = pricingCalculator.calculate(items);
        long id = repository.nextId();
        Order order = new Order(
            id,
            request.customerName(),
            items,
            OrderStatus.CREATED,
            pricing.subtotal(),
            pricing.discount(),
            pricing.tax(),
            pricing.total(),
            Instant.now()
        );
        return repository.save(order);
    }

    public Order getOrder(long id) {
        return repository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public PageResponse<Order> listOrders(OrderStatus status, String query, int page, int size) {
        if (page < 0) {
            throw new OrderValidationException(List.of(new ValidationError("page", "Page must be 0 or greater")));
        }
        if (size <= 0) {
            throw new OrderValidationException(List.of(new ValidationError("size", "Size must be greater than 0")));
        }

        List<Order> filtered = repository.findAll().stream()
            .filter(order -> status == null || order.getStatus() == status)
            .filter(order -> matchesQuery(order, query))
            .toList();

        int totalElements = filtered.size();
        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<Order> pageContent = filtered.subList(fromIndex, toIndex);
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(pageContent, page, size, totalElements, totalPages);
    }

    public Order shipOrder(long id) {
        Order order = getOrder(id);
        if (order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOrderStateException(id, order.getStatus(), "ship");
        }
        Order updated = order.withStatus(OrderStatus.SHIPPED);
        return repository.save(updated);
    }

    public Order cancelOrder(long id) {
        Order order = getOrder(id);
        if (order.getStatus() == OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException(id, order.getStatus(), "cancel");
        }
        Order updated = order.withStatus(OrderStatus.CANCELLED);
        return repository.save(updated);
    }

    private boolean matchesQuery(Order order, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String needle = query.toLowerCase(Locale.ROOT);
        boolean inCustomer = order.getCustomerName().toLowerCase(Locale.ROOT).contains(needle);
        boolean inItems = order.getItems().stream()
            .map(OrderItem::getName)
            .collect(Collectors.joining(" "))
            .toLowerCase(Locale.ROOT)
            .contains(needle);
        return inCustomer || inItems;
    }

    private void validateRequest(CreateOrderRequest request) {
        if (request == null) {
            throw new OrderValidationException(List.of(new ValidationError("order", "Order payload is required")));
        }

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            List<ValidationError> errors = violations.stream()
                .map(violation -> new ValidationError(
                    violation.getPropertyPath().toString(),
                    violation.getMessage()
                ))
                .toList();
            throw new OrderValidationException(errors);
        }
    }
}
