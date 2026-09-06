package com.example.payment.controller;

import com.example.payment.dto.request.CreatePaymentRequest;
import com.example.payment.dto.request.UpdatePaymentStatusRequest;
import com.example.payment.dto.response.ApiResponse;
import com.example.payment.dto.response.PaymentResponse;
import com.example.payment.entity.PaymentStatus;
import com.example.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> create(@Valid @RequestBody CreatePaymentRequest request,
            HttpServletRequest httpRequest) {
        PaymentResponse response = paymentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment created successfully", response, httpRequest.getRequestURI()));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getById(@PathVariable String paymentId,
            HttpServletRequest httpRequest) {

        PaymentResponse response = paymentService.getByPaymentId(paymentId);
        return ResponseEntity
                .ok(ApiResponse.success("Payment retrieved successfully", response, httpRequest.getRequestURI()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> search(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) PaymentStatus status,
            @PageableDefault(size = 20) Pageable pageable, HttpServletRequest httpRequest) {

        Page<PaymentResponse> response = paymentService.search(orderId, status, pageable);

        return ResponseEntity
                .ok(ApiResponse.success("Payment retrieved successfully", response, httpRequest.getRequestURI()));
    }

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<ApiResponse<PaymentResponse>> updateStatus(
            @PathVariable String paymentId,
            @Valid @RequestBody UpdatePaymentStatusRequest request, HttpServletRequest httpRequest) {
                PaymentResponse response = paymentService.updateStatus(paymentId, request);
        return ResponseEntity.ok(ApiResponse.success("Status updated", response, httpRequest.getRequestURI()));
    }
}