package com.example.sideProject.copon.Service;

import com.example.sideProject.copon.domain.Coupon;
import com.example.sideProject.copon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.example.sideProject.copon.dto.Coupon.*;

@Service
@RequiredArgsConstructor
public class CouponV2RedisService {
    private final StringRedisTemplate redisTemplate;
    private final CouponAsyncSaver couponAsyncSaver; // 비동기 저장 서비스
    private final CouponRepository couponRepository;

    // 쿠폰 발급 (Redis에서만 처리)
    public boolean issue(CouponIssueRequest request) {
        Long stock = redisTemplate.opsForValue().decrement(request.promotionId().toString());

        if (stock == null || stock < 0) {
            // 재고 부족 → 롤백
            redisTemplate.opsForValue().increment(request.promotionId().toString());
            return false;
        }

        // Redis 성공 시 DB 저장 요청만 위임
//        couponAsyncSaver.saveAsync(request.promotionId(), request.userId());

        Coupon coupon = Coupon.issued(request.promotionId(), request.userId());
        couponRepository.save(coupon);

        return true;
    }

    // 초기 재고 세팅
    public void initStock(Long promotionId, int stock) {
        redisTemplate.opsForValue().set(promotionId.toString(), String.valueOf(stock));
    }
}
