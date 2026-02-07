package com.example.api.orders;

import com.example.item.Item;
import com.example.member.Member;
import com.example.orders.Address;
import com.example.orders.CreateOrderRequest;
import com.example.orders.OrderItemRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager em;

    @Test
    void orderCreate() throws Exception {
        // Given test data
        ObjectMapper objectMapper = new ObjectMapper();

        Address address = new Address("Seoul", "마포구", "69050");

        Member member = new Member("Tester","테스트계정", address);
        em.persist(member);

        Item item = new Item("공동구매 가이드", 15000, 100);
        Item item2 = new Item("스프링부트4.0", 25000, 300);
        em.persist(item);
        em.persist(item2);

        em.flush();
        em.clear();

        OrderItemRequest itemRequest = new OrderItemRequest(item.getId(), 10);
        OrderItemRequest itemRequest2 = new OrderItemRequest(item2.getId(), 30);

        List<OrderItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequest);
        itemRequestList.add(itemRequest2);

        CreateOrderRequest request = new CreateOrderRequest(
                member.getId(),
                itemRequestList
        );

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.memberId").value(member.getId().toString()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].name").value("공동구매 가이드"))
                .andExpect(jsonPath("$.items[0].quantity").value(10))
                .andExpect(jsonPath("$.items[0].unitPrice").value(15000))
                .andExpect(jsonPath("$.items[0].lineTotal").value(150000))
                .andExpect(jsonPath("$.items[1].name").value("스프링부트4.0"))
                .andExpect(jsonPath("$.totalAmount").value(900000))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andDo(print());
    }
}
