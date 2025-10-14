package com.example.sideProject.copon.Service;

import com.example.sideProject.copon.repository.CouponRepository;
import com.example.sideProject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.example.sideProject.copon.dto.Coupon.*;

@Service
@RequiredArgsConstructor
public class CouponV2RedisService implements CouponStrategy {
    private final StringRedisTemplate redisTemplate;
    private final CouponAsyncSaver couponAsyncSaver; // 비동기 저장 서비스
    private final CouponRepository couponRepository;
    private final CouponIssueQueueService couponIssueQueueService;
    private final UserRepository userRepository;

    @Override
    public void issue(CouponIssueRequest request) {

        if (!userRepository.existsById(request.userId())) {
            throw new IllegalStateException("존재하지 않는 사용자입니다.");
        }

        Long stock = redisTemplate.opsForValue().decrement(request.promotionId().toString());

        if (stock == null || stock < 0) {
            redisTemplate.opsForValue().increment(request.promotionId().toString());
            throw new IllegalStateException("쿠폰 재고가 소진되었습니다.");
        }

        couponIssueQueueService.addQueue(request);
    }

    @Override
    public String getType() {
        return "redis";
    }

//    // 쿠폰 발급 (Redis 재고 차감 후, DB 저장은 대기열에 위임)
//    public boolean issue(CouponIssueRequest request) {
//
//        if (!userRepository.existsById(request.userId())) {
//            throw CouponErrorCode.NOT_FOUND_TOKEN.exception();
//        }
//
//        Long stock = redisTemplate.opsForValue().decrement(request.promotionId().toString());
//
//        if (stock == null || stock < 0) {
//            // 재고 부족 → 롤백
//            // 재고 부족 시 롤백 (경쟁 상태에 취약할 수 있음)
//            redisTemplate.opsForValue().increment(request.promotionId().toString());
//            return false;
//        }
//
////        // Redis 성공 시 DB 저장 요청만 위임
////        couponAsyncSaver.saveAsync(request.promotionId(), request.userId());
//
////        Coupon coupon = Coupon.issued(request.promotionId(), request.userId());
////        couponRepository.save(coupon);
//        // 재고 차감 성공 시, DB 저장을 위해 대기열에 요청 추가
//        couponIssueQueueService.addQueue(request);
//
//        return true;
//    }
//    public void initStock(Long promotionId, int stock) {
//        redisTemplate.opsForValue().set(promotionId.toString(), String.valueOf(stock));
//    }
}
