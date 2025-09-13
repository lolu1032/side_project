package com.example.sideProject.copon.Service;

import com.example.sideProject.copon.domain.Coupon;
import com.example.sideProject.copon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponAsyncSaver {
    private final CouponRepository couponRepository;

    @Async("taskExecutor") // thread pool 사용
    public void saveAsync(Long promotionId, Long userId) {
        Coupon coupon = Coupon.issued(promotionId, userId);
        couponRepository.save(coupon);
    }
}