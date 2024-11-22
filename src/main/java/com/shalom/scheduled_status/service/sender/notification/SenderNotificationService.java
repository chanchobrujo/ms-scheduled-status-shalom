package com.shalom.scheduled_status.service.sender.notification;

import com.shalom.scheduled_status.model.dto.NotificationDto;
import com.shalom.scheduled_status.properties.RedisProperties;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SenderNotificationService implements ISenderNotificationService {
    private final RedisProperties redisProperties;
    private final RedisCommands<String, String> lettuceRedisConnection;

    @Override
    public void sendNotification(NotificationDto notification) {
        var message = notification.toBodyMessage();
        String id = this.lettuceRedisConnection.xadd(this.redisProperties.getKey(), message);
        log.info("Message {} : {} posted", message, id);
    }
}
