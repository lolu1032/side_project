package com.example.sideProject.copon.Service;

import com.example.sideProject.copon.domain.Coupon;
import com.example.sideProject.copon.dto.Coupon.CouponIssueRequest;
import com.example.sideProject.copon.repository.CouponRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssueQueueConsumer {

    private final StringRedisTemplate redisTemplate;
    private final CouponRepository couponRepository;
    private final ObjectMapper objectMapper;

    private static final String MAIN_QUEUE_KEY = "coupon:issue-queue";
    private static final String PROCESSING_QUEUE_KEY = "coupon:issue-processing";
    private static final String DEAD_LETTER_QUEUE_KEY = "coupon:dead-letter-queue";
    private static final int BATCH_SIZE = 1;

    // 1초마다 실행 (fixedDelay = 1000)
    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void processQueue() {
        List<String> requestsJson = new ArrayList<>();
        // 1. RPOPLPUSH (or LMOVE)를 사용하여 작업을 안전하게 processing-queue로 이동
        for (int i = 0; i < BATCH_SIZE; i++) {
            String request = redisTemplate.opsForList().rightPopAndLeftPush(MAIN_QUEUE_KEY, PROCESSING_QUEUE_KEY);
            if (request == null) {
                break;
            }
            requestsJson.add(request);
        }

        if (requestsJson.isEmpty()) {
            return;
        }

        // 2. 가져온 작업을 Coupon 엔티티로 변환
        List<Coupon> couponsToSave = requestsJson.stream()
                .map(this::parseRequest)
                .filter(Objects::nonNull)
                .map(req -> Coupon.issued(req.promotionId(), req.userId()))
                .collect(Collectors.toList());

        // 3. DB에 배치 저장 (saveAll 사용)
        if (!couponsToSave.isEmpty()) {
            couponRepository.saveAll(couponsToSave);
        }

        // 4. 처리가 완료된 항목들을 processing-queue에서 제거
        // LREM key count value -> count=0 이면 모든 value를 지움
        // 마지막 요소 하나만 지워도, rightPopAndLeftPush 순서에 의해 모든 처리된 요소가 제거됨
        redisTemplate.opsForList().remove(PROCESSING_QUEUE_KEY, requestsJson.size(), requestsJson.get(requestsJson.size() - 1));
    }

    private CouponIssueRequest parseRequest(String requestJson) {
        try {
            return objectMapper.readValue(requestJson, CouponIssueRequest.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse coupon issue request from queue: {}", requestJson, e);
            // [개선] 파싱 실패 항목은 Dead Letter Queue로 이동
            redisTemplate.opsForList().leftPush(DEAD_LETTER_QUEUE_KEY, requestJson);
            return null;
        }
    }
}