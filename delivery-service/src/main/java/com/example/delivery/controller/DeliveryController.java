package com.example.delivery.controller;

import com.example.delivery.dto.request.CreateDeliveryRequest;
import com.example.delivery.dto.request.UpdateDeliveryStatusRequest;
import com.example.delivery.dto.response.ApiResponse;
import com.example.delivery.dto.response.DeliveryResponse;
import com.example.delivery.entity.DeliveryStatus;
import com.example.delivery.service.DeliveryService;

import jakarta.servlet.http.HttpServletRequest;
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
            @Valid @RequestBody CreateDeliveryRequest request, HttpServletRequest httpRequest) {
        DeliveryResponse response = deliveryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Delivery created", response, httpRequest.getRequestURI()));
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<ApiResponse<DeliveryResponse>> getById(@PathVariable String deliveryId,
            HttpServletRequest httpRequest) {
        DeliveryResponse response = deliveryService.getByDeliveryId(deliveryId);
        return ResponseEntity
                .ok(ApiResponse.success("Delivery retrieved successfully", response, httpRequest.getRequestURI()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DeliveryResponse>>> search(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) DeliveryStatus status,
            @PageableDefault(size = 20) Pageable pageable, HttpServletRequest httpRequest) {

        Page<DeliveryResponse> page = deliveryService.search(orderId, status, pageable);
        return ResponseEntity
                .ok(ApiResponse.success("Delivery retrieved successfully", page, httpRequest.getRequestURI()));
    }

    @PutMapping("/{deliveryId}/status")
    public ResponseEntity<ApiResponse<DeliveryResponse>> updateStatus(
            @PathVariable String deliveryId,
            @Valid @RequestBody UpdateDeliveryStatusRequest request, HttpServletRequest httpRequest) {

        DeliveryResponse response = deliveryService.updateStatus(deliveryId, request);
        return ResponseEntity.ok(ApiResponse.success("Status updated", response, httpRequest.getRequestURI()));
    }
}