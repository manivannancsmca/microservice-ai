package com.example.delivery.service;

import com.example.delivery.dto.request.CreateDeliveryRequest;
import com.example.delivery.dto.request.UpdateDeliveryStatusRequest;
import com.example.delivery.dto.response.DeliveryResponse;
import com.example.delivery.entity.Delivery;
import com.example.delivery.entity.DeliveryStatus;
import com.example.delivery.exception.BusinessException;
import com.example.delivery.exception.ResourceNotFoundException;
import com.example.delivery.mapper.DeliveryMapper;
import com.example.delivery.repository.DeliveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class DeliveryService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;

    public DeliveryService(DeliveryRepository deliveryRepository, DeliveryMapper deliveryMapper) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryMapper = deliveryMapper;
    }

    public DeliveryResponse create(CreateDeliveryRequest request) {
        log.info("Creating delivery for orderId={}", request.orderId());

        Delivery delivery = deliveryMapper.toEntity(request);
        delivery = deliveryRepository.save(delivery);

        log.info("Delivery created: {}", delivery.getDeliveryId());
        return deliveryMapper.toResponse(delivery);
    }

    @Transactional(readOnly = true)
    public DeliveryResponse getByDeliveryId(String deliveryId) {
        return deliveryMapper.toResponse(findEntity(deliveryId));
    }

    @Transactional(readOnly = true)
    public Page<DeliveryResponse> search(String orderId, DeliveryStatus status, Pageable pageable) {
        Page<Delivery> page;
        if (orderId != null && status != null) {
            page = deliveryRepository.findByOrderIdAndStatus(orderId, status, pageable);
        } else if (orderId != null) {
            page = deliveryRepository.findByOrderId(orderId, pageable);
        } else if (status != null) {
            page = deliveryRepository.findByStatus(status, pageable);
        } else {
            page = deliveryRepository.findAll(pageable);
        }
        return page.map(deliveryMapper::toResponse);
    }

    public DeliveryResponse updateStatus(String deliveryId, UpdateDeliveryStatusRequest request) {
        Delivery delivery = findEntity(deliveryId);
        validateStatusTransition(delivery.getStatus(), request.status());

        delivery.setStatus(request.status());

        if (request.trackingNumber() != null) {
            delivery.setTrackingNumber(request.trackingNumber());
        }
        if (request.failureReason() != null) {
            delivery.setFailureReason(request.failureReason());
        }
        if (request.status() == DeliveryStatus.DELIVERED) {
            delivery.setDeliveredAt(Instant.now());
        }

        delivery = deliveryRepository.save(delivery);
        log.info("Delivery {} status → {}", deliveryId, request.status());
        return deliveryMapper.toResponse(delivery);
    }

    private Delivery findEntity(String deliveryId) {
        return deliveryRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found: " + deliveryId));
    }

    private void validateStatusTransition(DeliveryStatus current, DeliveryStatus next) {
        boolean allowed = switch (current) {
            case PENDING -> next == DeliveryStatus.ASSIGNED || next == DeliveryStatus.CANCELLED;
            case ASSIGNED -> next == DeliveryStatus.IN_TRANSIT || next == DeliveryStatus.CANCELLED;
            case IN_TRANSIT -> next == DeliveryStatus.OUT_FOR_DELIVERY || next == DeliveryStatus.FAILED;
            case OUT_FOR_DELIVERY -> next == DeliveryStatus.DELIVERED || next == DeliveryStatus.FAILED;
            case DELIVERED, FAILED, CANCELLED -> false;
        };
        if (!allowed) {
            throw new BusinessException("Invalid status transition from " + current + " to " + next);
        }
    }
}