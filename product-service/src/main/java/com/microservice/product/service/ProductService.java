package com.microservice.product.service;


import com.microservice.product.dto.request.CreateProductRequest;
import com.microservice.product.dto.request.StatusUpdateRequest;
import com.microservice.product.dto.request.UpdateProductRequest;
import com.microservice.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse create(CreateProductRequest request);

    ProductResponse getById(Long id);

    Page<ProductResponse> getAll(String status, String category, String name, String sku, Pageable pageable);

    ProductResponse update(Long id, UpdateProductRequest request);

    ProductResponse updateStatus(Long id, StatusUpdateRequest request);

    void delete(Long id);
}