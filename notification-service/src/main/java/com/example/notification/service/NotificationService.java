package com.example.notification.service;

import com.example.notification.dto.request.CreateNotificationRequest;
import com.example.notification.dto.request.UpdateNotificationStatusRequest;
import com.example.notification.dto.response.NotificationResponse;
import com.example.notification.entity.Notification;
import com.example.notification.entity.NotificationStatus;
import com.example.notification.exception.BusinessException;
import com.example.notification.exception.ResourceNotFoundException;
import com.example.notification.mapper.NotificationMapper;
import com.example.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository,
            NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    public NotificationResponse create(CreateNotificationRequest request) {
        log.info("Creating notification for userId={}, type={}, channel={}",
                request.userId(), request.type(), request.channel());

        Notification notification = notificationMapper.toEntity(request);
        notification = notificationRepository.save(notification);

        log.info("Notification created: {}", notification.getNotificationId());
        return notificationMapper.toResponse(notification);
    }

    @Transactional(readOnly = true)
    public NotificationResponse getByNotificationId(String notificationId) {
        return notificationMapper.toResponse(findEntity(notificationId));
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> search(String userId,
            NotificationStatus status,
            Pageable pageable) {
        Page<Notification> page;
        if (userId != null && status != null) {
            page = notificationRepository.findByUserIdAndStatus(userId, status, pageable);
        } else if (userId != null) {
            page = notificationRepository.findByUserId(userId, pageable);
        } else if (status != null) {
            page = notificationRepository.findByStatus(status, pageable);
        } else {
            page = notificationRepository.findAll(pageable);
        }
        return page.map(notificationMapper::toResponse);
    }

    public NotificationResponse updateStatus(String notificationId,
            UpdateNotificationStatusRequest request) {
        Notification notification = findEntity(notificationId);
        validateStatusTransition(notification.getStatus(), request.status());

        notification.setStatus(request.status());
        if (request.failureReason() != null) {
            notification.setFailureReason(request.failureReason());
        }
        if (request.status() == NotificationStatus.SENT || request.status() == NotificationStatus.DELIVERED) {
            notification.setSentAt(Instant.now());
        }

        notification = notificationRepository.save(notification);
        log.info("Notification {} status → {}", notificationId, request.status());
        return notificationMapper.toResponse(notification);
    }

    public NotificationResponse retry(String notificationId) {
        Notification notification = findEntity(notificationId);
        if (notification.getStatus() != NotificationStatus.FAILED) {
            throw new BusinessException("Only FAILED notifications can be retried");
        }
        notification.setStatus(NotificationStatus.PENDING);
        notification.setFailureReason(null);
        notification.setSentAt(null);
        notification = notificationRepository.save(notification);
        log.info("Notification {} marked for retry", notificationId);
        return notificationMapper.toResponse(notification);
    }

    private Notification findEntity(String notificationId) {
        return notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
    }

    private void validateStatusTransition(NotificationStatus current, NotificationStatus next) {
        boolean allowed = switch (current) {
            case PENDING -> next == NotificationStatus.SENT || next == NotificationStatus.FAILED;
            case SENT -> next == NotificationStatus.DELIVERED || next == NotificationStatus.FAILED;
            case FAILED -> next == NotificationStatus.PENDING; // via retry
            case DELIVERED -> false;
        };
        if (!allowed) {
            throw new BusinessException("Invalid status transition from " + current + " to " + next);
        }
    }
}