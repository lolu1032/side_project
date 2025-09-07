package com.example.side_project.copon.Service;

import com.example.side_project.copon.repository.CouponsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CouponStockSyncService {

    private final CouponsRepository couponsRepository;
    private final CouponV2RedisService couponV2RedisService;

    @Scheduled(fixedDelay = 30_000)
    @Transactional
    // 엔티티의 변경을 감지하면 스케줄드 실행하기위해 Transactional 걸어둠
    // 이거 안거니까 그냥 30초 지나면 갯수가 0으로 업데이트를 해버림
    // 그거 방지할려고 @Transactional 걸음
    // 그랬더니 성공적임 마치 최종적 일관성이란걸 해본 느낌
    public void syncCouponStockToDb() {
        Set<String> issuedCouponKeys = couponV2RedisService.getAllIssuedCouponKeys();
        if (issuedCouponKeys == null || issuedCouponKeys.isEmpty()) return;

        for (String key : issuedCouponKeys) {
            Long couponId = Long.parseLong(key.split(":")[2]);

            couponsRepository.findById(couponId).ifPresent(coupon -> {
                Long issuedCount = couponV2RedisService.getIssuedCount(couponId);
                if (issuedCount != null && issuedCount > 0) {
                    coupon.decreaseQuantity(issuedCount.intValue());
                    couponsRepository.save(coupon);
                    couponV2RedisService.deleteCouponIssuedRecord(couponId);
                }
            });
        }
    }

}
