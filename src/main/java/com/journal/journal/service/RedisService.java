package com.journal.journal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public <T> T getValue(String key, Class<T> entityClass) {
        try {
            Object value = redisTemplate.opsForValue().get(key);

            if (value == null) {
                return null;
            }

            return objectMapper.readValue(value.toString(), entityClass);

        } catch (Exception e) {
            log.error("Error getting value from Redis: {}", e.getMessage());
            return null;
        }
    }

    public void setKey(String key, Object obj, Long expire) {
        try {
            String jsonValue = objectMapper.writeValueAsString(obj);

            redisTemplate.opsForValue().set(
                    key,
                    jsonValue,
                    expire,
                    TimeUnit.SECONDS
            );

        } catch (Exception e) {
            log.error("Error setting value in Redis: {}", e.getMessage());
        }
    }
}