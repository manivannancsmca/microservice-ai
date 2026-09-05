package com.microservice.stock.controller;

import com.microservice.stock.dto.request.*;
import com.microservice.stock.dto.response.ApiResponse;
import com.microservice.stock.dto.response.StockResponse;
import com.microservice.stock.service.StockService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StockResponse>> create(
            @Valid @RequestBody CreateStockRequest request,
            HttpServletRequest httpRequest) {

        StockResponse response = stockService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Stock created successfully", response, httpRequest.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StockResponse>> getById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        StockResponse response = stockService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Stock retrieved successfully", response, httpRequest.getRequestURI()));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<StockResponse>> getByProductId(
            @PathVariable Long productId,
            HttpServletRequest httpRequest) {

        StockResponse response = stockService.getByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success("Stock retrieved successfully", response, httpRequest.getRequestURI()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<StockResponse>>> getAll(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            HttpServletRequest httpRequest) {

        Page<StockResponse> page = stockService.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("Stocks retrieved successfully", page, httpRequest.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StockResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStockRequest request,
            HttpServletRequest httpRequest) {

        StockResponse response = stockService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully", response, httpRequest.getRequestURI()));
    }

    @PostMapping("/{productId}/reserve")
    public ResponseEntity<ApiResponse<StockResponse>> reserve(
            @PathVariable Long productId,
            @Valid @RequestBody ReserveStockRequest request,
            HttpServletRequest httpRequest) {

        StockResponse response = stockService.reserve(productId, request);
        return ResponseEntity.ok(ApiResponse.success("Stock reserved successfully", response, httpRequest.getRequestURI()));
    }

    @PostMapping("/{productId}/release")
    public ResponseEntity<ApiResponse<StockResponse>> release(
            @PathVariable Long productId,
            @Valid @RequestBody ReleaseStockRequest request,
            HttpServletRequest httpRequest) {

        StockResponse response = stockService.release(productId, request);
        return ResponseEntity.ok(ApiResponse.success("Stock released successfully", response, httpRequest.getRequestURI()));
    }
}
