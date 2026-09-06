package com.example.notification.mapper;

import com.example.notification.dto.request.CreateNotificationRequest;
import com.example.notification.dto.response.NotificationResponse;
import com.example.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "notificationId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Notification toEntity(CreateNotificationRequest request);

    NotificationResponse toResponse(Notification notification);
}