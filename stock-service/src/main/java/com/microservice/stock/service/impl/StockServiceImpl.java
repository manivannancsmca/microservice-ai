package com.microservice.stock.service.impl;


import com.microservice.stock.dto.request.*;
import com.microservice.stock.dto.response.StockResponse;
import com.microservice.stock.entity.Stock;
import com.microservice.stock.exception.DuplicateResourceException;
import com.microservice.stock.exception.InsufficientStockException;
import com.microservice.stock.exception.ResourceNotFoundException;
import com.microservice.stock.mapper.StockMapper;
import com.microservice.stock.repository.StockRepository;
import com.microservice.stock.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {

    private static final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;

    public StockServiceImpl(StockRepository stockRepository, StockMapper stockMapper) {
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
    }

    @Override
    @Transactional
    public StockResponse create(CreateStockRequest request) {
        log.info("Creating stock for productId={}", request.productId());

        if (stockRepository.existsByProductId(request.productId())) {
            throw new DuplicateResourceException(
                    "Stock already exists for productId: " + request.productId());
        }

        Stock stock = stockMapper.toEntity(request);
        Stock saved = stockRepository.save(stock);

        log.info("Stock created with id={} for productId={}", saved.getId(), saved.getProductId());
        return stockMapper.toResponse(saved);
    }

    @Override
    public StockResponse getById(Long id) {
        return stockMapper.toResponse(findByIdOrThrow(id));
    }

    @Override
    public StockResponse getByProductId(Long productId) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Stock not found for productId: " + productId));
        return stockMapper.toResponse(stock);
    }

    @Override
    public Page<StockResponse> getAll(Pageable pageable) {
        return stockRepository.findAll(pageable).map(stockMapper::toResponse);
    }

    @Override
    @Transactional
    public StockResponse update(Long id, UpdateStockRequest request) {
        Stock stock = findByIdOrThrow(id);
        stockMapper.updateEntity(request, stock);
        Stock updated = stockRepository.save(stock);
        log.info("Stock {} updated", id);
        return stockMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public StockResponse reserve(Long productId, ReserveStockRequest request) {
        log.info("Reserving {} units for productId={}", request.quantity(), productId);

        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Stock not found for productId: " + productId));

        try {
            stock.reserve(request.quantity());
        } catch (IllegalStateException ex) {
            throw new InsufficientStockException(
                    "Insufficient stock for productId " + productId +
                    ". Available: " + stock.getAvailableQuantity());
        }

        Stock saved = stockRepository.save(stock);
        log.info("Reserved {} units for productId={}. New available={}",
                request.quantity(), productId, saved.getAvailableQuantity());

        return stockMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public StockResponse release(Long productId, ReleaseStockRequest request) {
        log.info("Releasing {} units for productId={}", request.quantity(), productId);

        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Stock not found for productId: " + productId));

        try {
            stock.release(request.quantity());
        } catch (IllegalStateException ex) {
            throw new InsufficientStockException(ex.getMessage());
        }

        Stock saved = stockRepository.save(stock);
        log.info("Released {} units for productId={}. New available={}",
                request.quantity(), productId, saved.getAvailableQuantity());

        return stockMapper.toResponse(saved);
    }

    private Stock findByIdOrThrow(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found with id: " + id));
    }
}