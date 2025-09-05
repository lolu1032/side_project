package com.example.side_project.copon.Service;

import com.example.side_project.copon.domain.Coupons;
import com.example.side_project.copon.repository.CouponsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

        if(!couponV2RedisService.decreaseStock(couponId)) {
            return "쿠폰 재고가 없다";
        }

        couponIssuesV2Service.saveCouponIssue(couponId,userId);

        return "쿠폰 발급";
    }

}
