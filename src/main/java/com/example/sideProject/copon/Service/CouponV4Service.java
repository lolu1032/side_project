package com.example.sideProject.copon.Service;

import com.example.sideProject.copon.domain.Coupon;
import com.example.sideProject.copon.repository.CouponRepository;
import com.example.sideProject.copon.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import com.example.sideProject.copon.dto.Coupon.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class CouponV4Service {
    private static final HashMap<Long, AtomicInteger> stockMap = new HashMap<>();
    private final PromotionRepository promotionRepository;
    private final CouponRepository couponRepository;

    static {
        stockMap.put(1L, new AtomicInteger(100));
    }

    public void issue(CouponIssueRequest request) {
        decreaseStock(request.promotionId());

        var promotion = promotionRepository.findById(request.promotionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로모션 ID: " + request.promotionId()));

        if (!promotion.isActive()) {
            throw new IllegalStateException("프로모션 기간이 아닙니다.");
        }

        var issuedCoupon = Coupon.issued(request.promotionId(), request.userId());
        couponRepository.save(issuedCoupon);
    }

    public void decreaseStock(Long promotionId) {
        AtomicInteger stock = stockMap.get(promotionId);
        while (true) {
            int current = stock.get();
            if (current <= 0) throw new IllegalStateException("재고 없음");

            if (stock.compareAndSet(current, current - 1)) {
                break;
            }
        }
    }
}
