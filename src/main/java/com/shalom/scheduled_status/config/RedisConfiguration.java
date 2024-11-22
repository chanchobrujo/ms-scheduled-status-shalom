package com.shalom.scheduled_status.config;

import com.shalom.scheduled_status.properties.RedisProperties;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.lettuce.core.RedisClient.create;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {
    private final RedisProperties properties;

    @Bean
    public RedisCommands<String, String> lettuceRedisConnection() {
        return create(this.properties.getUri())
                .connect()
                .sync();
    }
}
