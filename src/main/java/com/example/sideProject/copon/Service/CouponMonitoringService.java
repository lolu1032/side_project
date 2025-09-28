package com.example.sideProject.copon.Service;

import com.example.sideProject.copon.dto.Coupon.CouponQueueStatusResponse;
import com.example.sideProject.copon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponMonitoringService {

    private final StringRedisTemplate redisTemplate;
    private final CouponRepository couponRepository;

    private static final String MAIN_QUEUE_KEY = "coupon:issue-queue";

    /**
     * 현재 쿠폰 시스템의 상태를 조회합니다.
     * @param promotionId 프로모션 ID
     * @return 현재 재고, 대기열 크기, DB 저장 수
     */
    public CouponQueueStatusResponse getSystemStatus(Long promotionId) {
        String stockStr = redisTemplate.opsForValue().get(String.valueOf(promotionId));
        Long stock = (stockStr != null) ? Long.parseLong(stockStr) : 0L;

        Long queueSize = redisTemplate.opsForList().size(MAIN_QUEUE_KEY);
        if (queueSize == null) {
            queueSize = 0L;
        }

        long dbCount = couponRepository.count();

        return new CouponQueueStatusResponse(stock, queueSize, dbCount);
    }
}