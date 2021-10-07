package com.B305.ogym.common.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/*
    회원탈퇴한 유저의 JWT TOKEN을 만료시키기 위해서 사용하는 Redis 저장소에 대한 환경설정
 */

@Configuration
public class RedisBlackListConfig {

    @Value("${spring.redis.blacklist.host}")
    private String redisHost;

    @Value("${spring.redis.blacklist.port}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisBlackListConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisBlackListConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

}
