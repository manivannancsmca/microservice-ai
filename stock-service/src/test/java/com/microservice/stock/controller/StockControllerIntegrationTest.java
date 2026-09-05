package com.microservice.stock.controller;


import com.microservice.stock.dto.request.CreateStockRequest;
import com.microservice.stock.dto.request.ReleaseStockRequest;
import com.microservice.stock.dto.request.ReserveStockRequest;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
@Testcontainers
class StockControllerIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("stock_db")
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
    void fullStockFlow() throws Exception {
        // 1. Create stock
        CreateStockRequest createRequest = new CreateStockRequest(200L, 100);

        String createResponse = mockMvc.perform(post("/api/v1/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productId").value(200))
                .andExpect(jsonPath("$.data.availableQuantity").value(100))
                .andExpect(jsonPath("$.data.reservedQuantity").value(0))
                .andReturn().getResponse().getContentAsString();

        // 2. Get by productId
        mockMvc.perform(get("/api/v1/stocks/product/{productId}", 200L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.availableQuantity").value(100));

        // 3. Reserve 20 units
        ReserveStockRequest reserveRequest = new ReserveStockRequest(20);
        mockMvc.perform(post("/api/v1/stocks/{productId}/reserve", 200L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserveRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.availableQuantity").value(80))
                .andExpect(jsonPath("$.data.reservedQuantity").value(20));

        // 4. Release 5 units
        ReleaseStockRequest releaseRequest = new ReleaseStockRequest(5);
        mockMvc.perform(post("/api/v1/stocks/{productId}/release", 200L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(releaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.availableQuantity").value(85))
                .andExpect(jsonPath("$.data.reservedQuantity").value(15));

        // 5. Try to reserve more than available → should fail
        ReserveStockRequest tooMuch = new ReserveStockRequest(1000);
        mockMvc.perform(post("/api/v1/stocks/{productId}/reserve", 200L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tooMuch)))
                .andExpect(status().isConflict());
    }

    @Test
    void create_duplicateProductId_returns409() throws Exception {
        CreateStockRequest request = new CreateStockRequest(300L, 50);

        mockMvc.perform(post("/api/v1/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}