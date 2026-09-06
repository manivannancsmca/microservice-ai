package com.example.order.service.impl;

import com.example.order.dto.request.CreateOrderItemRequest;
import com.example.order.dto.request.CreateOrderRequest;
import com.example.order.dto.request.UpdateOrderStatusRequest;
import com.example.order.dto.response.OrderResponse;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.exception.ResourceNotFoundException;
import com.example.order.mapper.OrderMapper;
import com.example.order.repository.OrderRepository;
import com.example.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        log.info("Creating order for userId={}", request.userId());

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(request.userId());
        order.setStatus("PENDING");
        order.setCurrency("USD");

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CreateOrderItemRequest itemRequest : request.items()) {
            OrderItem item = orderMapper.toOrderItem(itemRequest);
            order.addItem(item);
            totalAmount = totalAmount.add(item.getTotalPrice());
        }

        order.setTotalAmount(totalAmount);

        Order saved = orderRepository.save(order);
        log.info("Order created successfully: id={}, orderNumber={}", saved.getId(), saved.getOrderNumber());

        return orderMapper.toResponse(saved);
    }

    @Override
    public OrderResponse getById(Long id) {
        Order order = findByIdOrThrow(id);
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse getByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with orderNumber: " + orderNumber));
        return orderMapper.toResponse(order);
    }

    @Override
    public Page<OrderResponse> getAll(Long userId, String status, Pageable pageable) {
        return orderRepository.findByFilters(userId, status, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = findByIdOrThrow(id);
        order.setStatus(request.status());
        Order updated = orderRepository.save(order);
        log.info("Order {} status changed to {}", id, request.status());
        return orderMapper.toResponse(updated);
    }

    private Order findByIdOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private String generateOrderNumber() {
        // Simple unique order number: ORD-yyyyMMddHHmmss-XXXX
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .format(Instant.now().atZone(java.time.ZoneOffset.UTC));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ORD-" + timestamp + "-" + random;
    }
}