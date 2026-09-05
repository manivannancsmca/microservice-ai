package com.microservice.stock.mapper;

import com.microservice.stock.dto.request.CreateStockRequest;
import com.microservice.stock.dto.request.UpdateStockRequest;
import com.microservice.stock.dto.response.StockResponse;
import com.microservice.stock.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StockMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservedQuantity", constant = "0")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Stock toEntity(CreateStockRequest request);

    StockResponse toResponse(Stock stock);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntity(UpdateStockRequest request, @MappingTarget Stock stock);
}