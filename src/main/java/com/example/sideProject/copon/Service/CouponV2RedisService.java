package com.example.sideProject.copon.Service;

import com.example.sideProject.copon.repository.CouponRepository;
import com.example.sideProject.exception.CouponErrorCode;
import com.example.sideProject.exception.UserErrorCode;
import com.example.sideProject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import com.example.sideProject.copon.dto.Coupon.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponV2RedisService implements CouponStrategy {
    private final StringRedisTemplate redisTemplate;
    private final CouponRepository couponRepository;
    private final CouponIssueQueueService couponIssueQueueService;
    private final UserRepository userRepository;

    private static final DefaultRedisScript<Long> ISSUE_SCRIPT;

    static {
        ISSUE_SCRIPT = new DefaultRedisScript<>();
        ISSUE_SCRIPT.setResultType(Long.class);
        ISSUE_SCRIPT.setScriptText("""
            local stock = tonumber(redis.call("GET", KEYS[1]))
            if not stock or stock <= 0 then
                return 0
            end
            redis.call("DECR", KEYS[1])
            return 1
        """);
    }
    @Override
    public boolean issue(CouponIssueRequest request) {

        if (!userRepository.existsById(request.userId())) {
            throw UserErrorCode.NOT_FOUNT_USERNAME.exception();
        }
        if(couponRepository.existsById(request.userId())) {
            throw CouponErrorCode.ALREADY_ISSUED_COUPON.exception();
        }
        Long result = redisTemplate.execute(
                ISSUE_SCRIPT,
                List.of(request.promotionId().toString())
        );

        if (result == null || result == 0) {
            throw CouponErrorCode.SOLD_OUT_COUPON.exception();
        }

        couponIssueQueueService.addQueue(request);
        return true;
    }

    @Override
    public String getName() {
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
    public void initStock(Long promotionId, int stock) {
        redisTemplate.opsForValue().set(promotionId.toString(), String.valueOf(stock));
    }
}
