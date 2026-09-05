package com.microservice.stock.service;

import com.microservice.stock.dto.request.*;
import com.microservice.stock.dto.response.StockResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockService {

    StockResponse create(CreateStockRequest request);

    StockResponse getById(Long id);

    StockResponse getByProductId(Long productId);

    Page<StockResponse> getAll(Pageable pageable);

    StockResponse update(Long id, UpdateStockRequest request);

    StockResponse reserve(Long productId, ReserveStockRequest request);

    StockResponse release(Long productId, ReleaseStockRequest request);
}