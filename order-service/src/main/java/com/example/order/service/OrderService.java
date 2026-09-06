package com.example.order.service;

import com.example.order.dto.request.CreateOrderRequest;
import com.example.order.dto.request.UpdateOrderStatusRequest;
import com.example.order.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse create(CreateOrderRequest request);

    OrderResponse getById(Long id);

    OrderResponse getByOrderNumber(String orderNumber);

    Page<OrderResponse> getAll(Long userId, String status, Pageable pageable);

    OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request);
}