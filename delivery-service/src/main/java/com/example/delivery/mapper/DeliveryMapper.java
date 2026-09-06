package com.example.delivery.mapper;

import com.example.delivery.dto.request.CreateDeliveryRequest;
import com.example.delivery.dto.response.DeliveryResponse;
import com.example.delivery.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deliveryId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "estimatedDelivery", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Delivery toEntity(CreateDeliveryRequest request);

    DeliveryResponse toResponse(Delivery delivery);
}