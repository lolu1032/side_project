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
public class CouponV4Service implements CouponStrategy {
    private static final HashMap<Long, AtomicInteger> stockMap = new HashMap<>();
    private final PromotionRepository promotionRepository;
    private final CouponRepository couponRepository;

    static {
        stockMap.put(1L, new AtomicInteger(100));
    }

//    public void issue(Long userId, Long promotionId) {
//        decreaseStock(promotionId);
//
//        var promotion = promotionRepository.findById(promotionId).get();
//        if (!promotion.isActive()) {
//            throw new IllegalStateException("프로모션 기간이 아닙니다.");
//        }
//        var issuedCoupon = Coupon.issued(promotionId, userId);
//        couponRepository.save(issuedCoupon);
//    }

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

    @Override
    public void issue(CouponIssueRequest request) {
        decreaseStock(request.promotionId());

        var promotion = promotionRepository.findById(request.promotionId()).get();
        if (!promotion.isActive()) {
            throw new IllegalStateException("프로모션 기간이 아닙니다.");
        }
        var issuedCoupon = Coupon.issued(request.promotionId(), request.userId());
        couponRepository.save(issuedCoupon);
    }

    @Override
    public String getType() {
        return "atomic";
    }
}
