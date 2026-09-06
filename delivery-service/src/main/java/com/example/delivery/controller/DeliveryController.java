package com.example.delivery.controller;

import com.example.delivery.dto.request.CreateDeliveryRequest;
import com.example.delivery.dto.request.UpdateDeliveryStatusRequest;
import com.example.delivery.dto.response.ApiResponse;
import com.example.delivery.dto.response.DeliveryResponse;
import com.example.delivery.entity.DeliveryStatus;
import com.example.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DeliveryResponse>> create(
            @Valid @RequestBody CreateDeliveryRequest request) {
        DeliveryResponse response = deliveryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Delivery created", response));
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<ApiResponse<DeliveryResponse>> getById(@PathVariable String deliveryId) {
        return ResponseEntity.ok(ApiResponse.ok(deliveryService.getByDeliveryId(deliveryId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DeliveryResponse>>> search(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) DeliveryStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(deliveryService.search(orderId, status, pageable)));
    }

    @PutMapping("/{deliveryId}/status")
    public ResponseEntity<ApiResponse<DeliveryResponse>> updateStatus(
            @PathVariable String deliveryId,
            @Valid @RequestBody UpdateDeliveryStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Status updated",
                deliveryService.updateStatus(deliveryId, request)));
    }
}