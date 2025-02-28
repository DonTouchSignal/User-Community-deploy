package com.example.user.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 레디스 db에 대한 연결 처리(데이터쓰기,추출,...)
 */
@Configuration
public class RedisConfig {
    // 프로퍼티 값 명시적으로 주입
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    // 레디스와 연동(결) 관리하는 객체를 빈으로 구성
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 명시적으로 레디스 연결 구성
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        redisConfig.setPassword(redisPassword);

        // 명시적 설정을 사용하여 연결 팩토리 생성
        return new LettuceConnectionFactory(redisConfig);
    }

    // 레디스 데이터를 처리하는 객체를 빈으로 구성
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory()); // 연결설정
        // 설정
        template.setKeySerializer(new StringRedisSerializer()); // 키를 저정할때 문자열 객체 직렬화를 통해서 처리
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // 값 객체 직렬화로 처리
        return template;
    }
}