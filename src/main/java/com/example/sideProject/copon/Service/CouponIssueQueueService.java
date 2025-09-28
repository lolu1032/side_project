package com.example.sideProject.copon.Service;

import com.example.sideProject.copon.dto.Coupon.CouponIssueRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueQueueService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // 대기열의 Key로 사용할 상수
    private static final String QUEUE_KEY = "coupon:issue-queue";

    /**
     * 쿠폰 발급 요청을 대기열에 추가합니다.
     * @param request 쿠폰 발급 요청 DTO
     */
    public void addQueue(CouponIssueRequest request) {
        try {
            String requestJson = objectMapper.writeValueAsString(request);
            redisTemplate.opsForList().rightPush(QUEUE_KEY, requestJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize coupon issue request: {}", request, e);
            // [개선] 실패한 요청은 별도의 Dead Letter Queue(DLQ)로 보내 재처리나 분석 기회를 가질 수 있습니다.
        }
    }
}