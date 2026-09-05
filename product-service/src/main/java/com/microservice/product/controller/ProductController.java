package com.microservice.product.controller;

import com.microservice.product.dto.request.CreateProductRequest;
import com.microservice.product.dto.request.StatusUpdateRequest;
import com.microservice.product.dto.request.UpdateProductRequest;
import com.microservice.product.dto.response.ApiResponse;
import com.microservice.product.dto.response.ProductResponse;
import com.microservice.product.service.ProductService;
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
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody CreateProductRequest request,
            HttpServletRequest httpRequest) {

        ProductResponse response = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", response, httpRequest.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        ProductResponse response = productService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", response, httpRequest.getRequestURI()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sku,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest httpRequest) {

        Page<ProductResponse> page = productService.getAll(status, category, name, sku, pageable);
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", page, httpRequest.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request,
            HttpServletRequest httpRequest) {

        ProductResponse response = productService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", response, httpRequest.getRequestURI()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ProductResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request,
            HttpServletRequest httpRequest) {

        ProductResponse response = productService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product status updated successfully", response, httpRequest.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", httpRequest.getRequestURI()));
    }
}