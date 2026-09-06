package com.example.order.mapper;

import com.example.order.dto.request.CreateOrderItemRequest;
import com.example.order.dto.response.OrderItemResponse;
import com.example.order.dto.response.OrderResponse;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "totalPrice", expression = "java(request.unitPrice().multiply(java.math.BigDecimal.valueOf(request.quantity())))")
    OrderItem toOrderItem(CreateOrderItemRequest request);

    OrderItemResponse toOrderItemResponse(OrderItem item);

    List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> items);

    @Mapping(target = "items", source = "items")
    OrderResponse toResponse(Order order);
}