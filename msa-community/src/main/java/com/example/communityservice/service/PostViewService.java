package com.example.communityservice.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.communityservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostViewService {
    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;
    
    // Redis에서 조회수 가져오기
    public int getViewCount(Integer postId) {
        String key = "post:view:" + postId;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.parseInt(value) : 0;
    }
    
    // Redis에 조회수 증가하고 현재 조회수 반환
    public int increaseAndGetViewCount(Integer postId) {
        String key = "post:view:" + postId;
        Long newCount = redisTemplate.opsForValue().increment(key);
        return newCount != null ? newCount.intValue() : 1;
    }
    
    // Redis에 초기 조회수 설정 (게시글 첫 조회 시)
    public void initializeViewCount(Integer postId, int dbViewCount) {
        String key = "post:view:" + postId;
        if (redisTemplate.opsForValue().get(key) == null) {
            redisTemplate.opsForValue().set(key, String.valueOf(dbViewCount));
        }
    }
    
    // 5분마다 Redis -> DB 동기화
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void syncViewCountToDb() {
        Set<String> keys = redisTemplate.keys("post:view:*");
        if (keys == null) return;
        
        for (String key : keys) {
            String postId = key.replace("post:view:", "");
            String viewCount = redisTemplate.opsForValue().get(key);
            
            if (viewCount != null) {
                postRepository.updateViewCount(
                    Integer.parseInt(postId), 
                    Integer.parseInt(viewCount)
                );
            }
        }
    }
    
    public void deleteViewCount(Integer postId) {
        String key = "post:view:" + postId;
        redisTemplate.delete(key);
    }
} 