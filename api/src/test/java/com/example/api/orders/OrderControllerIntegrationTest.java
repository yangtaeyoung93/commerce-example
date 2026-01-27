package com.example.api.orders;

import com.example.infra.orders.InMemoryOrderRepository;
import com.example.orders.CreateOrderRequest;
import com.example.orders.Order;
import com.example.orders.OrderItemRequest;
import com.example.orders.OrderPricingCalculator;
import com.example.orders.OrderRepository;
import com.example.orders.OrderService;
import com.example.orders.OrderValidator;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository repository;

    @BeforeEach
    void resetRepository() {
        if (repository instanceof InMemoryOrderRepository inMemory) {
            inMemory.clear();
        }
    }

    @Test
    void createOrder_returnsCreatedOrder() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
            "Kim",
            List.of(new OrderItemRequest("Book", 2, new BigDecimal("15.00")))
        );

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.customerName").value("Kim"))
            .andExpect(jsonPath("$.status").value("CREATED"))
            .andExpect(jsonPath("$.total").value(32.10));
    }

    @Test
    void getOrder_returnsOrderById() throws Exception {
        OrderService service = new OrderService(repository, new OrderValidator(), new OrderPricingCalculator());
        Order order = service.createOrder(new CreateOrderRequest(
            "Lee",
            List.of(new OrderItemRequest("Pen", 1, new BigDecimal("5.00")))
        ));

        mockMvc.perform(get("/orders/{id}", order.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(order.getId()))
            .andExpect(jsonPath("$.customerName").value("Lee"));
    }

    @Test
    void listOrders_filtersByStatus() throws Exception {
        OrderService service = new OrderService(repository, new OrderValidator(), new OrderPricingCalculator());
        Order first = service.createOrder(new CreateOrderRequest(
            "Park",
            List.of(new OrderItemRequest("Notebook", 1, new BigDecimal("12.00")))
        ));
        service.shipOrder(first.getId());
        service.createOrder(new CreateOrderRequest(
            "Choi",
            List.of(new OrderItemRequest("Pencil", 1, new BigDecimal("3.00")))
        ));

        mockMvc.perform(get("/orders")
                .param("status", "CREATED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].customerName").value("Choi"));
    }

    @Test
    void listOrders_filtersByQuery() throws Exception {
        OrderService service = new OrderService(repository, new OrderValidator(), new OrderPricingCalculator());
        service.createOrder(new CreateOrderRequest(
            "Alpha",
            List.of(new OrderItemRequest("Notebook", 1, new BigDecimal("12.00")))
        ));
        service.createOrder(new CreateOrderRequest(
            "Bravo",
            List.of(new OrderItemRequest("Marker", 1, new BigDecimal("3.00")))
        ));

        mockMvc.perform(get("/orders")
                .param("q", "note"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].customerName").value("Alpha"));
    }

    @Test
    void createOrder_returnsValidationError() throws Exception {
        Map<String, Object> payload = Map.of(
            "customerName", "",
            "items", List.of()
        );

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("ORDER_VALIDATION_FAILED"));
    }
}
