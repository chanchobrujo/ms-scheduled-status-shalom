package com.shalom.scheduled_status.service.sender.notification;

import com.shalom.scheduled_status.model.dto.NotificationDto;

public interface ISenderNotificationService {
    void sendNotification(NotificationDto notification);
}
