package com.example.side_project.copon.Service;

import com.example.side_project.copon.domain.Coupons;
import com.example.side_project.copon.repository.CouponsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class CouponV2Service {
    private final CouponV2RedisService couponV2RedisService;
    private final CouponIssuesV2Service couponIssuesV2Service;
    private final CouponsRepository couponsRepository;

    public String issueCoupon(Long couponId, Long userId) {
        if(!couponV2RedisService.checkDuplicate(couponId,userId)) {
            return "이미 발급해버림";
        }
        if(couponV2RedisService.decreaseStock(couponId)) {
            return "쿠폰 재고가 없다";
        }

        couponIssuesV2Service.saveCouponIssue(couponId,userId);

        return "쿠폰 발급";
    }

    private Coupons createUpdatedCoupon(Long couponId) {
        return couponsRepository.findById(couponId)
                .map(c -> Coupons.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .quantity(0)
                        .discountRate(c.getDiscountRate())
                        .startsAt(c.getStartsAt())
                        .build())
                .orElse(null);
    }

    @Scheduled(fixedDelay = 30000)
    private void updateIssuedCoupons() {
        Set<String> keys = couponV2RedisService.getAllIssuedCouponKeys();
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            Long couponId = Long.valueOf(key.split(":")[2]);
            Coupons updatedCoupon = createUpdatedCoupon(couponId);
            if (updatedCoupon != null) {
                couponsRepository.save(updatedCoupon); // DB 반영
            }
            couponV2RedisService.deleteCouponIssuedRecord(couponId); // Redis 삭제
        }
    }
}
