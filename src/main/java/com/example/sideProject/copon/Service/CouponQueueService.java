package com.example.sideProject.copon.Service;

import com.example.sideProject.copon.constant.QueueConstants;
import com.example.sideProject.copon.dto.Coupon.*;
import com.example.sideProject.copon.dto.Queue.*;
import com.example.sideProject.exception.CouponErrorCode;
import com.example.sideProject.exception.QueueErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponQueueService {
    private final StringRedisTemplate redisTemplate;
    private final CouponV2RedisService couponV2RedisService;

    private static final String WAITING_QUEUE_KEY = "coupon:waiting_queue:";
    private static final String PROCESSING_SET_KEY = "coupon:processing:";
    private static final String USER_STATUS_KEY = "coupon:user_status:";
    private static final int BATCH_SIZE = 100;

    /**
     * 사용자를 대기열(Queue)에 추가
     * 리펙토링 순위 1
     */
    public QueuePosition addToQueue(Long promotionId, Long userId) {
        String queueKey = WAITING_QUEUE_KEY + promotionId;
        String userStatusKey = USER_STATUS_KEY + promotionId + ":" + userId;

        // 이미 대기열에 있는지 확인
        String existingStatus = redisTemplate.opsForValue().get(userStatusKey);
        if (existingStatus != null) {
            long position = getQueuePosition(promotionId, userId);
            return new QueuePosition(position, existingStatus);
        }

        // 큐 오른쪽 끝에 사용자 추가 (FIFO)
        redisTemplate.opsForList().rightPush(queueKey, userId.toString());

        // 사용자 상태 저장
        redisTemplate.opsForValue().set(userStatusKey, QueueErrorCode.WAITING.name(), Duration.ofHours(1));

        // 현재 대기 순번 조회 (큐에서의 위치)
        long position = getQueuePosition(promotionId, userId);

        return new QueuePosition(position, QueueErrorCode.WAITING.name());
    }

    /**
     * 큐에서 사용자의 위치 찾기
     */
    private long getQueuePosition(Long promotionId, Long userId) {
        String queueKey = WAITING_QUEUE_KEY + promotionId;
        List<String> queue = redisTemplate.opsForList().range(queueKey, 0, -1);

        if (queue != null) {
            for (int i = 0; i < queue.size(); i++) {
                if (userId.toString().equals(queue.get(i))) {
                    return i + 1; // 1부터 시작하는 순번
                }
            }
        }
        return 0;
    }

    /**
     * 대기열 상태 조회
     * 리펙토링 순위 2
     */
    public QueueStatus getQueueStatus(Long promotionId, Long userId) {
        String queueKey = WAITING_QUEUE_KEY + promotionId;
        String processingKey = PROCESSING_SET_KEY + promotionId;
        String userStatusKey = USER_STATUS_KEY + promotionId + ":" + userId;

        // 사용자 상태 확인
        String userStatus = redisTemplate.opsForValue().get(userStatusKey);

        if (userStatus == null) {
            return new QueueStatus(QueueErrorCode.NOT_IN_QUEUE.name(), 0, 0, QueueErrorCode.NOT_IN_QUEUE.message());
        }

        switch (userStatus) {
            case QueueConstants.STATUS_COMPLETED:
                return new QueueStatus(QueueConstants.STATUS_COMPLETED, 0, 0, CouponErrorCode.COMPLETED.message());
            case QueueConstants.STATUS_FAILED:
                return new QueueStatus(QueueConstants.STATUS_FAILED, 0, 0, CouponErrorCode.SOLD_OUT_COUPON.message());
            case QueueConstants.STATUS_PROCESSING:
                return new QueueStatus(QueueConstants.STATUS_PROCESSING, 0, 0, CouponErrorCode.PROCESSING.message());
        }

        // 대기 중인 경우 위치 확인
        long position = getQueuePosition(promotionId, userId);
        if (position > 0) {
            long totalWaiting = redisTemplate.opsForList().size(queueKey);

            // 예상 대기시간 계산 (1초에 100명씩 처리)
            long estimatedWaitTime = Math.max(1, (position - 1) / BATCH_SIZE + 1);
            String message = String.format("대기 순번: %d번, 예상 대기시간: 약 %d초", position, estimatedWaitTime);

            return new QueueStatus(QueueErrorCode.WAITING.name(), position, totalWaiting, message);
        }

        return new QueueStatus(QueueErrorCode.NOT_IN_QUEUE.name(), 0, 0, QueueErrorCode.NOT_IN_QUEUE.message());
    }

    /**
     * 대기열에서 배치 단위로 사용자를 처리
     */
    @Scheduled(fixedDelay = 1000) // 1초마다 실행
    public void processBatch() {
        Set<String> queueKeys = redisTemplate.keys(WAITING_QUEUE_KEY + "*");

        if (queueKeys != null) {
            for (String queueKey : queueKeys) {
                String promotionId = queueKey.replace(WAITING_QUEUE_KEY, "");
                try {
                    processBatchForPromotion(Long.parseLong(promotionId));
                } catch (NumberFormatException e) {
                    log.error("잘못된 프로모션 ID: {}", promotionId);
                }
            }
        }
    }

    private void processBatchForPromotion(Long promotionId) {
        String queueKey = WAITING_QUEUE_KEY + promotionId;
        String processingKey = PROCESSING_SET_KEY + promotionId;

        // 현재 남은 재고 확인
        String stockStr = redisTemplate.opsForValue().get(promotionId.toString());
        if (stockStr == null) return;

        int remainingStock = Integer.parseInt(stockStr);
        if (remainingStock <= 0) {
            // 재고가 없으면 남은 대기자들을 모두 실패 처리
            handleNoStock(promotionId);
            return;
        }

        // 처리할 인원 결정 (남은 재고와 배치 크기 중 작은 값)
        int batchSize = Math.min(BATCH_SIZE, remainingStock);

        // 큐 길이 확인
        long queueLength = redisTemplate.opsForList().size(queueKey);
        if (queueLength == 0) return;

        // 실제 처리할 인원 (큐 길이와 배치 크기 중 작은 값)
        int actualBatchSize = (int) Math.min(batchSize, queueLength);

        log.info("프로모션 {} 배치 처리 시작: {}명 (남은 재고: {}, 대기인원: {})",
                promotionId, actualBatchSize, remainingStock, queueLength);

        // 큐 왼쪽에서 배치 크기만큼 사용자 꺼내기 (FIFO)
        for (int i = 0; i < actualBatchSize; i++) {
            String userIdStr = redisTemplate.opsForList().leftPop(queueKey);
            if (userIdStr == null) break;

            Long userId = Long.parseLong(userIdStr);

            // 처리 중 세트에 추가
            redisTemplate.opsForSet().add(processingKey, userIdStr);

            // 사용자 상태 업데이트
            String userStatusKey = USER_STATUS_KEY + promotionId + ":" + userId;
            redisTemplate.opsForValue().set(userStatusKey, QueueConstants.STATUS_PROCESSING, Duration.ofHours(1));

            // 비동기로 쿠폰 발급 처리
            processUserCouponAsync(promotionId, userId);
        }
    }

    private void handleNoStock(Long promotionId) {
        String queueKey = WAITING_QUEUE_KEY + promotionId;

        // 큐에 남은 모든 사용자 조회
        List<String> remainingUsers = redisTemplate.opsForList().range(queueKey, 0, -1);

        if (remainingUsers != null && !remainingUsers.isEmpty()) {
            for (String userIdStr : remainingUsers) {
                Long userId = Long.parseLong(userIdStr);
                String userStatusKey = USER_STATUS_KEY + promotionId + ":" + userId;
                redisTemplate.opsForValue().set(userStatusKey, QueueConstants.STATUS_FAILED, Duration.ofHours(1));
            }

            // 큐 클리어
            redisTemplate.delete(queueKey);

            log.info("프로모션 {} 재고 소진으로 {}명의 대기자 실패 처리", promotionId, remainingUsers.size());
        }
    }

//    @Async("taskExecutor")
//    public void processUserCouponAsync(Long promotionId, Long userId) {
//        String processingKey = PROCESSING_SET_KEY + promotionId;
//        String userStatusKey = USER_STATUS_KEY + promotionId + ":" + userId;
//        String userIdStr = userId.toString();
//
//        try {
//            CouponIssueRequest request = new CouponIssueRequest(promotionId, userId);
//            boolean success = couponV2RedisService.issue(request);
//
//            if (success) {
//                redisTemplate.opsForValue().set(userStatusKey, QueueType.COMPLETED.name(), Duration.ofHours(1));
//                log.info("사용자 {} 쿠폰 발급 성공", userId);
//            } else {
//                redisTemplate.opsForValue().set(userStatusKey, QueueType.FAILED.name(), Duration.ofHours(1));
//                log.info("사용자 {} 쿠폰 발급 실패 (재고 부족)", userId);
//            }
//        } catch (Exception e) {
//            redisTemplate.opsForValue().set(userStatusKey, QueueType.FAILED.name(), Duration.ofHours(1));
//            log.error("사용자 {} 쿠폰 발급 중 오류 발생", userId, e);
//        } finally {
//            // 처리 완료 후 처리 중 세트에서 제거
//            redisTemplate.opsForSet().remove(processingKey, userIdStr);
//        }
//    }
    @Async("taskExecutor")
    public void processUserCouponAsync(Long promotionId, Long userId) {
        String processingKey = PROCESSING_SET_KEY + promotionId;
        String userStatusKey = USER_STATUS_KEY + promotionId + ":" + userId;
        String userIdStr = userId.toString();

        try {
            CouponIssueRequest request = new CouponIssueRequest(promotionId, userId);
            couponV2RedisService.issue(request); // void 호출

            // 성공하면
            redisTemplate.opsForValue().set(userStatusKey, QueueConstants.STATUS_COMPLETED, Duration.ofHours(1));
            log.info("사용자 {} 쿠폰 발급 성공", userId);
        } catch (IllegalStateException e) {
            // 실패하면
            redisTemplate.opsForValue().set(userStatusKey, QueueConstants.STATUS_FAILED, Duration.ofHours(1));
            log.info("사용자 {} 쿠폰 발급 실패: {}", userId, e.getMessage());
        } catch (Exception e) {
            // 기타 예외 처리
            redisTemplate.opsForValue().set(userStatusKey, QueueConstants.STATUS_FAILED, Duration.ofHours(1));
            log.error("사용자 {} 쿠폰 발급 중 오류 발생", userId, e);
        } finally {
            // 처리 완료 후 처리 중 세트에서 제거
            redisTemplate.opsForSet().remove(processingKey, userIdStr);
        }
    }

    @Scheduled(fixedDelay = 600000)
    public void deleteRedis() {
        Set<String> keys = redisTemplate.keys(USER_STATUS_KEY+"*");
        if(!keys.isEmpty() && keys != null) {
            redisTemplate.delete(keys);
        }
    }
    /**
     * 재고 초기화
     */
    public void initStock(Long promotionId, int stock) {
        redisTemplate.opsForValue().set(promotionId.toString(), String.valueOf(stock));
        log.info("프로모션 {} 재고 초기화: {}개", promotionId, stock);
    }

    /**
     * 현재 재고 조회
     */
    public int getCurrentStock(Long promotionId) {
        String stockStr = redisTemplate.opsForValue().get(promotionId.toString());
        return stockStr != null ? Integer.parseInt(stockStr) : 0;
    }

    /**
     * 대기열 길이 조회
     */
    public long getQueueLength(Long promotionId) {
        String queueKey = WAITING_QUEUE_KEY + promotionId;
        return redisTemplate.opsForList().size(queueKey);
    }

    /**
     * 처리 중인 사용자 수 조회
     */
    public long getProcessingCount(Long promotionId) {
        String processingKey = PROCESSING_SET_KEY + promotionId;
        return redisTemplate.opsForSet().size(processingKey);
    }
}
