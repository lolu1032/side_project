package com.example.side_project.service;

import com.example.side_project.domain.Coupon_issues;
import com.example.side_project.domain.Coupons;
import com.example.side_project.dto.Coupon.*;
import com.example.side_project.repository.CouponIssuesRepository;
import com.example.side_project.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssuesRepository couponIssuesRepository;

    public void couponList() {

        List<Coupons> all = couponRepository.findAll();

        if(all.isEmpty()) {
            throw new IllegalArgumentException("쿠폰이 존재하지않습니다.");
        }

    }

    public void getCoupon(CouponRequest request) {
        Coupons coupon = couponRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰이 존재하지 않습니다."));

        if(coupon.getQuantity() <= 0) {
            throw new IllegalArgumentException("쿠폰이 모두 소진됐습니다.");
        }

        if(couponIssuesRepository.existsByUserIdAndCouponId(request.userId(), request.id())){
            throw new IllegalArgumentException("이미 해당 쿠폰을 발급받았습니다.");
        }

        coupon.decreaseQuantity();

        Coupon_issues build = Coupon_issues.builder()
                .coupon_id(request.id())
                .user_id(request.userId())
                .is_used(false)
                .build();

        couponIssuesRepository.save(build);

    }

    public void issuanceCoupon(CouponsRequest request) {
        Coupons build = Coupons.builder()
                .name(request.name())
                .discount_rate(request.discount_rate())
                .quantity(request.quantity())
                .build();

        couponRepository.save(build);
    }

}
