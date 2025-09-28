package com.example.sideProject.copon.Controller;

import com.example.sideProject.copon.domain.Coupon;
import com.example.sideProject.copon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/v2")
@RequiredArgsConstructor
public class CouponQueueController {

    private final StringRedisTemplate redisTemplate;
    private final CouponRepository couponRepository;

    private static final String QUEUE_KEY_PREFIX = "coupon:queue:";
    private static final String READY_KEY_PREFIX = "coupon:ready:";
    private static final String STOCK_KEY_PREFIX = "coupon:stock:";

    /**
     * 1. 대기열 등록
     */
    @PostMapping("/{promotionId}/enqueue")
    public ResponseEntity<Map<String, Object>> enqueue(
            @PathVariable Long promotionId,
            @RequestParam String userId) {

        String queueKey = QUEUE_KEY_PREFIX + promotionId;

        // score = timestamp → 먼저 요청한 사람이 먼저 처리됨
        double score = (double) System.currentTimeMillis();
        redisTemplate.opsForZSet().add(queueKey, userId, score);

        Long rank = redisTemplate.opsForZSet().rank(queueKey, userId);
        Long size = redisTemplate.opsForZSet().size(queueKey);

        return ResponseEntity.ok(Map.of(
                "status", "ENQUEUED",
                "position", rank != null ? rank + 1 : null,
                "total", size
        ));
    }

    /**
     * 2. 내 순번 확인 (폴링용)
     */
    @GetMapping("/{promotionId}/queue-position/{userId}")
    public ResponseEntity<Map<String, Object>> getQueuePosition(
            @PathVariable Long promotionId,
            @PathVariable String userId) {

        String queueKey = QUEUE_KEY_PREFIX + promotionId;
        String stockKey = STOCK_KEY_PREFIX + promotionId;

        String stockStr = redisTemplate.opsForValue().get(stockKey);
        long stock = stockStr != null ? Long.parseLong(stockStr) : 0L;

        if (stock <= 0) {
            return ResponseEntity.ok(Map.of("status", "SOLD_OUT"));
        }

        Long rank = redisTemplate.opsForZSet().rank(queueKey, userId);
        Long size = redisTemplate.opsForZSet().size(queueKey);

        if (rank == null) {
            return ResponseEntity.ok(Map.of("status", "NOT_IN_QUEUE"));
        }

        return ResponseEntity.ok(Map.of(
                "status", "WAITING",
                "position", rank + 1,
                "total", size
        ));
    }

    /**
     * 3. 쿠폰 발급 버튼
     */
    @PostMapping("/{promotionId}/issue")
    public ResponseEntity<Map<String, Object>> issueCoupon(
            @PathVariable Long promotionId,
            @RequestParam Long userId) {

        String readyKey = READY_KEY_PREFIX + promotionId;
        String stockKey = STOCK_KEY_PREFIX + promotionId;

        Boolean isReady = redisTemplate.opsForSet().isMember(readyKey, userId);
        if (isReady == null || !isReady) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "아직 발급 차례가 아닙니다."));
        }

        Long stock = redisTemplate.opsForValue().decrement(stockKey);
        if (stock == null || stock < 0) {
            redisTemplate.opsForValue().increment(stockKey); // 롤백
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "재고가 소진되었습니다."));
        }

        // ✅ DB 저장
        couponRepository.save(Coupon.issued(promotionId, userId));

        // ✅ 큐에서 제거 + readySet 제거
        redisTemplate.opsForZSet().remove(QUEUE_KEY_PREFIX + promotionId, userId);
        redisTemplate.opsForSet().remove(readyKey, userId);

        return ResponseEntity.ok(Map.of("message", "쿠폰 발급 성공"));
    }

    /**
     * 4. 스케줄러 → 맨 앞 사람을 READY 상태로 이동
     */
    @Scheduled(fixedDelay = 1000)
    public void markReadyUsers() {
        Long promotionId = 1L; // TODO: 여러 프로모션이면 loop 처리 필요
        String queueKey = QUEUE_KEY_PREFIX + promotionId;
        String readyKey = READY_KEY_PREFIX + promotionId;
        String stockKey = STOCK_KEY_PREFIX + promotionId;

        String stockStr = redisTemplate.opsForValue().get(stockKey);
        long stock = stockStr != null ? Long.parseLong(stockStr) : 0L;
        if (stock <= 0) return;

        Set<String> users = redisTemplate.opsForZSet().range(queueKey, 0, 0);
        if (users == null || users.isEmpty()) return;

        String nextUser = users.iterator().next();

        // 발급 가능 집합에 넣음
        redisTemplate.opsForSet().add(readyKey, nextUser);
    }

    /**
     * 5. 재고 초기화 (테스트용)
     */
    @PostMapping("/{promotionId}/init-stock")
    public ResponseEntity<String> initPromotionStock(
            @PathVariable Long promotionId,
            @RequestParam(defaultValue = "100") int stock) {

        redisTemplate.opsForValue().set(STOCK_KEY_PREFIX + promotionId, String.valueOf(stock));
        return ResponseEntity.ok("프로모션 ID " + promotionId + "의 재고가 " + stock + "개로 설정되었습니다.");
    }
}
