package com.microservice.product.service;


import com.microservice.product.dto.request.CreateProductRequest;
import com.microservice.product.dto.request.StatusUpdateRequest;
import com.microservice.product.dto.request.UpdateProductRequest;
import com.microservice.product.dto.response.ProductResponse;
import com.microservice.product.entity.Product;
import com.microservice.product.exception.DuplicateResourceException;
import com.microservice.product.exception.ResourceNotFoundException;
import com.microservice.product.mapper.ProductMapper;
import com.microservice.product.repository.ProductRepository;
import com.microservice.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setSku("SKU-001");
        product.setName("Wireless Mouse");
        product.setPrice(new BigDecimal("29.99"));
        product.setCategory("ELECTRONICS");
        product.setStatus("ACTIVE");
        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());

        productResponse = new ProductResponse(
                1L, "SKU-001", "Wireless Mouse", null,
                new BigDecimal("29.99"), "ELECTRONICS", null,
                "ACTIVE", product.getCreatedAt(), product.getUpdatedAt()
        );
    }

    @Test
    @DisplayName("Should create product successfully")
    void create_success() {
        CreateProductRequest request = new CreateProductRequest(
                "SKU-001", "Wireless Mouse", "Ergonomic mouse",
                new BigDecimal("29.99"), "ELECTRONICS", "Logitech");

        when(productRepository.existsBySku("SKU-001")).thenReturn(false);
        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.create(request);

        assertThat(result.sku()).isEqualTo("SKU-001");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw when SKU already exists")
    void create_duplicateSku() {
        CreateProductRequest request = new CreateProductRequest(
                "SKU-001", "Wireless Mouse", null,
                new BigDecimal("29.99"), "ELECTRONICS", null);

        when(productRepository.existsBySku("SKU-001")).thenReturn(true);

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should return product by id")
    void getById_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.getById(1L);
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw when product not found")
    void getById_notFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should soft delete product")
    void delete_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productService.delete(1L);

        assertThat(product.getStatus()).isEqualTo("INACTIVE");
        verify(productRepository).save(product);
    }
}