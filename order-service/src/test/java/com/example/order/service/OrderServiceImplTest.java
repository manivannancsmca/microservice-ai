package com.example.order.service;

import com.example.order.dto.request.CreateOrderItemRequest;
import com.example.order.dto.request.CreateOrderRequest;
import com.example.order.dto.request.UpdateOrderStatusRequest;
import com.example.order.dto.response.OrderResponse;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.exception.ResourceNotFoundException;
import com.example.order.mapper.OrderMapper;
import com.example.order.repository.OrderRepository;
import com.example.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORD-20260906120000-ABCD");
        order.setUserId(10L);
        order.setStatus("PENDING");
        order.setTotalAmount(new BigDecimal("59.98"));
        order.setCurrency("USD");
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());

        OrderItem item = new OrderItem();
        item.setProductId(100L);
        item.setProductName("Wireless Mouse");
        item.setProductSku("SKU-001");
        item.setUnitPrice(new BigDecimal("29.99"));
        item.setQuantity(2);
        item.setTotalPrice(new BigDecimal("59.98"));
        order.addItem(item);

        orderResponse = new OrderResponse(
                1L, "ORD-20260906120000-ABCD", 10L, "PENDING",
                new BigDecimal("59.98"), "USD", List.of(), Instant.now(), Instant.now()
        );
    }

    @Test
    @DisplayName("Should create order successfully")
    void create_success() {
        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest(
                100L, "Wireless Mouse", "SKU-001",
                new BigDecimal("29.99"), 2);

        CreateOrderRequest request = new CreateOrderRequest(10L, List.of(itemRequest));

        when(orderMapper.toOrderItem(itemRequest)).thenReturn(order.getItems().get(0));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        OrderResponse result = orderService.create(request);

        assertThat(result.orderNumber()).isNotBlank();
        assertThat(result.status()).isEqualTo("PENDING");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should return order by id")
    void getById_success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        OrderResponse result = orderService.getById(1L);
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw when order not found")
    void getById_notFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should update order status")
    void updateStatus_success() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest("CONFIRMED");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        OrderResponse result = orderService.updateStatus(1L, request);

        assertThat(result).isNotNull();
        assertThat(order.getStatus()).isEqualTo("CONFIRMED");
    }
}