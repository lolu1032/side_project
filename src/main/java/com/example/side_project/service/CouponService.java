package com.example.side_project.service;

import com.example.side_project.domain.Coupon_issues;
import com.example.side_project.domain.Coupons;
import com.example.side_project.dto.Coupon.*;
import com.example.side_project.repository.CouponIssuesRepository;
import com.example.side_project.repository.CouponRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


@Service
@RequiredArgsConstructor
public class CouponService {

    private final Queue<Coupon_issues> buffer = new ConcurrentLinkedQueue<>();
    private final CouponRepository couponRepository;
    private final CouponIssuesRepository couponIssuesRepository;

    public List<Coupons> couponList() {

        List<Coupons> all = couponRepository.findAll();

        if(all.isEmpty()) {
            throw new IllegalArgumentException("쿠폰이 존재하지않습니다.");
        }

        return all;
    }

    @Scheduled(fixedRate = 3000)
    public void flushToDB() {
        List<Coupon_issues> toSave = new ArrayList<>();
        while (!buffer.isEmpty()) {
            toSave.add(buffer.poll());
        }

        if (!toSave.isEmpty()) {
            couponIssuesRepository.saveAll(toSave);
        }
    }

    public void enqueue(Coupon_issues issue) {
        buffer.add(issue);
    }

    @Transactional
    public void getCoupon(CouponRequest request) {

        int updated = couponRepository.decreaseQuantitySafely(request.id());

        if (updated == 0) {
            throw new IllegalArgumentException("쿠폰이 모두 소진됐습니다.");
        }

        enqueue(Coupon_issues.builder()
                .couponId(request.id())
                .userId(request.userId())
                .is_used(false)
                .build());

    }

    public void issuanceCoupon(CouponsRequest request) {
        Coupons build = Coupons.builder()
                .name(request.name())
                .discount_rate(request.discount_rate())
                .quantity(request.quantity())
                .starts_at(Instant.now())
                .build();

        couponRepository.save(build);
    }

    public void allGetCoupon() {
        /**
         * 모든 유저에게 쿠폰 발급 로직
         */
    }

}
