package com.shalom.scheduled_status.service;

import com.shalom.scheduled_status.model.dto.NotificationDto;

public interface ISenderNotificationService {
    void sendNotification(NotificationDto notification);
}
