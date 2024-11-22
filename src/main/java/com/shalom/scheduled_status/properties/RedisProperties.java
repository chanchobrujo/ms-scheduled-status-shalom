package com.shalom.scheduled_status.properties;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@ToString
@Configuration
public class RedisProperties {
    @Value("${spring.redis-listener.uri}")
    private String uri;
    @Value("${spring.redis-listener.key}")
    private String key;
    @Value("${spring.redis-listener.group}")
    private String group;
}
