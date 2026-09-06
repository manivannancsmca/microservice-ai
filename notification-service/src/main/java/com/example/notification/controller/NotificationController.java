package com.example.notification.controller;

import com.example.notification.dto.request.CreateNotificationRequest;
import com.example.notification.dto.request.UpdateNotificationStatusRequest;
import com.example.notification.dto.response.ApiResponse;
import com.example.notification.dto.response.NotificationResponse;
import com.example.notification.entity.NotificationStatus;
import com.example.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> create(
            @Valid @RequestBody CreateNotificationRequest request) {
        NotificationResponse response = notificationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notification created", response));
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getById(
            @PathVariable String notificationId) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getByNotificationId(notificationId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> search(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) NotificationStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.search(userId, status, pageable)));
    }

    @PutMapping("/{notificationId}/status")
    public ResponseEntity<ApiResponse<NotificationResponse>> updateStatus(
            @PathVariable String notificationId,
            @Valid @RequestBody UpdateNotificationStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Status updated",
                notificationService.updateStatus(notificationId, request)));
    }

    @PostMapping("/{notificationId}/retry")
    public ResponseEntity<ApiResponse<NotificationResponse>> retry(
            @PathVariable String notificationId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Notification queued for retry",
                notificationService.retry(notificationId)));
    }
}