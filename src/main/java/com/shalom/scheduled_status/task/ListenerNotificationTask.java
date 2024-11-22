package com.shalom.scheduled_status.task;

import com.shalom.scheduled_status.properties.RedisProperties;
import com.shalom.scheduled_status.service.sender.SenderMessageService;
import io.lettuce.core.Consumer;
import io.lettuce.core.RedisBusyException;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XReadArgs;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.lettuce.core.XReadArgs.StreamOffset.from;
import static io.lettuce.core.XReadArgs.StreamOffset.lastConsumed;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListenerNotificationTask {
    private final RedisProperties redisProperties;
    private final SenderMessageService senderMessageService;
    private final RedisCommands<String, String> lettuceRedisConnection;

    private String getKey() {
        return this.redisProperties.getKey();
    }

    private String getGroup() {
        return this.redisProperties.getGroup();
    }

    private void createGroup() {
        try {
            this.lettuceRedisConnection.xgroupCreate(from(this.getKey(), "0-0"), this.getGroup());
        } catch (RedisBusyException redisBusyException) {
            log.error(redisBusyException.getMessage());
        }
    }

    @Scheduled(cron = "${application.listenerNotificationTask}", zone = "America/Lima")
    public void listenerNotificationTask() {
        this.createGroup();
        Consumer<String> consumer = Consumer.from(this.getGroup(), "consumer_1");
        XReadArgs.StreamOffset<String> sds = lastConsumed(this.getKey());
        List<StreamMessage<String, String>> messages = this.lettuceRedisConnection.xreadgroup(consumer, sds);
        messages.forEach(message -> {
            this.senderMessageService.send(message.getBody());
            this.lettuceRedisConnection.xack(this.getKey(), this.getGroup(), message.getId());
        });
    }
}
