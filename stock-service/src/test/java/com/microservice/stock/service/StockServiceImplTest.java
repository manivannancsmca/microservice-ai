package com.microservice.stock.service;

import com.microservice.stock.dto.request.CreateStockRequest;
import com.microservice.stock.dto.request.ReleaseStockRequest;
import com.microservice.stock.dto.request.ReserveStockRequest;
import com.microservice.stock.dto.response.StockResponse;
import com.microservice.stock.entity.Stock;
import com.microservice.stock.exception.DuplicateResourceException;
import com.microservice.stock.exception.InsufficientStockException;
import com.microservice.stock.exception.ResourceNotFoundException;
import com.microservice.stock.mapper.StockMapper;
import com.microservice.stock.repository.StockRepository;
import com.microservice.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockMapper stockMapper;

    @InjectMocks
    private StockServiceImpl stockService;

    private Stock stock;
    private StockResponse stockResponse;

    @BeforeEach
    void setUp() {
        stock = new Stock();
        stock.setId(1L);
        stock.setProductId(100L);
        stock.setAvailableQuantity(50);
        stock.setReservedQuantity(0);
        stock.setCreatedAt(Instant.now());
        stock.setUpdatedAt(Instant.now());
        stock.setVersion(0L);

        stockResponse = new StockResponse(
                1L, 100L, 50, 0,
                stock.getCreatedAt(), stock.getUpdatedAt()
        );
    }

    @Test
    @DisplayName("Should create stock successfully")
    void create_success() {
        CreateStockRequest request = new CreateStockRequest(100L, 50);

        when(stockRepository.existsByProductId(100L)).thenReturn(false);
        when(stockMapper.toEntity(request)).thenReturn(stock);
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);
        when(stockMapper.toResponse(stock)).thenReturn(stockResponse);

        StockResponse result = stockService.create(request);

        assertThat(result.productId()).isEqualTo(100L);
        assertThat(result.availableQuantity()).isEqualTo(50);
    }

    @Test
    @DisplayName("Should throw when stock already exists for product")
    void create_duplicate() {
        CreateStockRequest request = new CreateStockRequest(100L, 50);
        when(stockRepository.existsByProductId(100L)).thenReturn(true);

        assertThatThrownBy(() -> stockService.create(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("Should reserve stock successfully")
    void reserve_success() {
        ReserveStockRequest request = new ReserveStockRequest(10);

        when(stockRepository.findByProductId(100L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(stock)).thenReturn(stock);
        when(stockMapper.toResponse(stock)).thenReturn(
                new StockResponse(1L, 100L, 40, 10, Instant.now(), Instant.now())
        );

        StockResponse result = stockService.reserve(100L, request);

        assertThat(result.availableQuantity()).isEqualTo(40);
        assertThat(result.reservedQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should throw when insufficient stock")
    void reserve_insufficient() {
        ReserveStockRequest request = new ReserveStockRequest(100);

        when(stockRepository.findByProductId(100L)).thenReturn(Optional.of(stock));

        assertThatThrownBy(() -> stockService.reserve(100L, request))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    @DisplayName("Should release stock successfully")
    void release_success() {
        stock.setAvailableQuantity(40);
        stock.setReservedQuantity(10);

        ReleaseStockRequest request = new ReleaseStockRequest(5);

        when(stockRepository.findByProductId(100L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(stock)).thenReturn(stock);
        when(stockMapper.toResponse(stock)).thenReturn(
                new StockResponse(1L, 100L, 45, 5, Instant.now(), Instant.now())
        );

        StockResponse result = stockService.release(100L, request);

        assertThat(result.availableQuantity()).isEqualTo(45);
        assertThat(result.reservedQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should throw when stock not found by productId")
    void getByProductId_notFound() {
        when(stockRepository.findByProductId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.getByProductId(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}