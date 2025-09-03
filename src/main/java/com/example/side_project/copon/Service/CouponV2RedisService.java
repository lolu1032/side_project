package com.example.side_project.copon.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponV2RedisService {
    // Spring Data Redis에서 제공하는 RedisTemplate의 특화버전
    // 특징
    // 키와 값 모두 String로 고정, 직렬화 없이 바로 사용 가능
    private final StringRedisTemplate redisTemplate;
    // static final을 통한 재사용성을 높힘
    private static final String COUPON_STOCK_KEY = "coupon:stock:";
    private static final String COUPON_USER_KEY = "coupon:user:";

    // 재고 차감
    public boolean decreaseStock(Long couponId) {
        String key = COUPON_STOCK_KEY + couponId;
        Long stock = redisTemplate.opsForValue().decrement(key);
        if (stock != null && stock >= 0) {
            return true;
        } else {
            redisTemplate.opsForValue().increment(key);
            return false;
        }
    }

    // 유저 중복 발급 체크
    public boolean checkDuplicate(Long couponId, Long userId) {
        String key = COUPON_USER_KEY + couponId;
        Long addedCount = redisTemplate.opsForSet().add(key, String.valueOf(userId));
        return addedCount != null && addedCount > 0;
    }
}
