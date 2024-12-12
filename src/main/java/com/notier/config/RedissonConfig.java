package com.notier.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    private static final String REDISSON_HOST_PREFIX = "redis://";
    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        RedissonClient redisson = null;
        Config config = new Config();
        config.useSingleServer()
            .setConnectionPoolSize(64)
            .setConnectionMinimumIdleSize(16)
            .setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort);

        config.setCodec(new JsonJacksonCodec());

        redisson = Redisson.create(config);
        return redisson;
    }


}
