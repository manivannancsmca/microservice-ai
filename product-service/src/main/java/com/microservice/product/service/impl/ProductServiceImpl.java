package com.microservice.product.service.impl;


import com.microservice.product.dto.request.CreateProductRequest;
import com.microservice.product.dto.request.StatusUpdateRequest;
import com.microservice.product.dto.request.UpdateProductRequest;
import com.microservice.product.dto.response.ProductResponse;
import com.microservice.product.entity.Product;
import com.microservice.product.exception.DuplicateResourceException;
import com.microservice.product.exception.ResourceNotFoundException;
import com.microservice.product.mapper.ProductMapper;
import com.microservice.product.repository.ProductRepository;
import com.microservice.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        log.info("Creating product with SKU={}", request.sku());

        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Product with SKU " + request.sku() + " already exists");
        }

        Product product = productMapper.toEntity(request);
        Product saved = productRepository.save(product);

        log.info("Product created successfully with id={}", saved.getId());
        return productMapper.toResponse(saved);
    }

    @Override
    public ProductResponse getById(Long id) {
        return productMapper.toResponse(findProductOrThrow(id));
    }

    @Override
    public Page<ProductResponse> getAll(String status, String category, String name, String sku, Pageable pageable) {
        return productRepository.findByFilters(status, category, name, sku, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = findProductOrThrow(id);
        productMapper.updateEntity(request, product);
        Product updated = productRepository.save(product);
        log.info("Product {} updated", id);
        return productMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public ProductResponse updateStatus(Long id, StatusUpdateRequest request) {
        Product product = findProductOrThrow(id);
        product.setStatus(request.status());
        Product updated = productRepository.save(product);
        log.info("Product {} status changed to {}", id, request.status());
        return productMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = findProductOrThrow(id);
        product.setStatus("INACTIVE");
        productRepository.save(product);
        log.info("Product {} soft-deleted", id);
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}