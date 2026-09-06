package com.example.delivery.repository;

import com.example.delivery.entity.Delivery;
import com.example.delivery.entity.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByDeliveryId(String deliveryId);

    Page<Delivery> findByOrderId(String orderId, Pageable pageable);

    Page<Delivery> findByStatus(DeliveryStatus status, Pageable pageable);

    Page<Delivery> findByOrderIdAndStatus(String orderId, DeliveryStatus status, Pageable pageable);

    Optional<Delivery> findByTrackingNumber(String trackingNumber);
}