package com.example.order.controller;

import com.example.order.dto.request.CreateOrderItemRequest;
import com.example.order.dto.request.CreateOrderRequest;
import com.example.order.dto.request.UpdateOrderStatusRequest;
//import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
@Testcontainers
class OrderControllerIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("order_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullOrderFlow() throws Exception {
        // 1. Create order
        CreateOrderItemRequest item = new CreateOrderItemRequest(
                100L, "Wireless Mouse", "SKU-001",
                new BigDecimal("29.99"), 2);

        CreateOrderRequest createRequest = new CreateOrderRequest(10L, List.of(item));

        String createResponse = mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").isNotEmpty())
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.totalAmount").value(59.98))
                .andExpect(jsonPath("$.data.items", hasSize(1)))
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(createResponse).path("data").path("id").asLong();
        String orderNumber = objectMapper.readTree(createResponse).path("data").path("orderNumber").asText();

        // 2. Get by id
        mockMvc.perform(get("/api/v1/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderNumber").value(orderNumber));

        // 3. Get by order number
        mockMvc.perform(get("/api/v1/orders/number/{orderNumber}", orderNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(orderId));

        // 4. Update status
        UpdateOrderStatusRequest statusRequest = new UpdateOrderStatusRequest("CONFIRMED");
        mockMvc.perform(patch("/api/v1/orders/{id}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));

        // 5. List orders
        mockMvc.perform(get("/api/v1/orders")
                        .param("userId", "10")
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{id}", 99999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}