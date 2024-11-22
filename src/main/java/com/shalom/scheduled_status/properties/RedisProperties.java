package com.shalom.scheduled_status.properties;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@ToString
@Configuration
public class RedisProperties {
    @Value("${spring.redis.uri}")
    private String uri;
    @Value("${spring.redis.key}")
    private String key;
    @Value("${spring.redis.group}")
    private String group;
}
