package com.example.sideProject.copon.Service;

import com.example.sideProject.copon.domain.Coupon;
import com.example.sideProject.copon.repository.CouponRepository;
import com.example.sideProject.copon.repository.PromotionRepository;
import com.example.sideProject.exception.CouponErrorCode;
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
        stockMap.put(2L, new AtomicInteger(100));
        stockMap.put(3L, new AtomicInteger(100));
        stockMap.put(4L, new AtomicInteger(100));
        stockMap.put(5L, new AtomicInteger(100));
        stockMap.put(6L, new AtomicInteger(100));
        stockMap.put(7L, new AtomicInteger(100));
        stockMap.put(8L, new AtomicInteger(100));
        stockMap.put(9L, new AtomicInteger(100));
        stockMap.put(10L, new AtomicInteger(100));
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
            if (current <= 0) throw CouponErrorCode.SOLD_OUT_COUPON.exception();

            if (stock.compareAndSet(current, current - 1)) {
                break;
            }
        }
    }

    @Override
    public boolean issue(CouponIssueRequest request) {
        if(couponRepository.existsByUserIdAndPromotionId(request.userId(),request.promotionId())) {
            throw CouponErrorCode.ISSUED_COUPON.exception();
        }
        decreaseStock(request.promotionId());

        var promotion = promotionRepository.findById(request.promotionId()).get();
        if (!promotion.isActive()) {
            throw CouponErrorCode.PROMOTION_NOT_ACTIVE.exception();
        }
        var issuedCoupon = Coupon.issued(request.promotionId(), request.userId());
        couponRepository.save(issuedCoupon);
        return true;
    }

    @Override
    public String getName() {
        return "atomic";
    }
}
