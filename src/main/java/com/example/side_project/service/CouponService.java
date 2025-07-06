package com.example.side_project.service;

import com.example.side_project.domain.Coupon_issues;
import com.example.side_project.domain.Coupons;
import com.example.side_project.domain.Users;
import com.example.side_project.dto.Coupon;
import com.example.side_project.dto.Coupon.*;
import com.example.side_project.exception.CouponErrorCode;
import com.example.side_project.repository.CouponIssuesRepository;
import com.example.side_project.repository.CouponRepository;
import com.example.side_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


@Service
@RequiredArgsConstructor
public class CouponService {

    private final Queue<Coupon_issues> buffer = new ConcurrentLinkedQueue<>();
    private final CouponRepository couponRepository;
    private final CouponIssuesRepository couponIssuesRepository;
    private final UserRepository userRepository;

    public List<Coupons> couponList() {

        List<Coupons> all = couponRepository.findAll();

        if(all.isEmpty()) {
            throw CouponErrorCode.NOT_FOUND_COUPON.exception();
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
    public CouponIssueResponse getCoupon(CouponRequest request) {

        int updated = couponRepository.decreaseQuantitySafely(request.id());

        if (updated == 0) {
            throw CouponErrorCode.SOLD_OUT_COUPON.exception();
        }

        Coupon_issues issue = Coupon_issues.builder()
                .couponId(request.id())
                .userId(request.userId())
                .is_used(false)
                .build();

        enqueue(issue);

        Coupons coupon = couponRepository.findById(request.id())
                .orElseThrow(() -> CouponErrorCode.NOT_FOUND_COUPON.exception());

        return CouponIssueResponse.builder()
                .uuid(issue.getUuid()) // prePersist 후 uuid 자동 생성됨
                .name(coupon.getName())
                .discountRate(coupon.getDiscount_rate())
                .issuedAt(issue.getIssued_at())
                .expiredAt(issue.getExpired_at())
                .build();
    }

    public CouponsResponse issuanceCoupon(CouponsRequest request) {
        Coupons build = Coupons.builder()
                .name(request.name())
                .discount_rate(request.discount_rate())
                .quantity(request.quantity())
                .starts_at(Instant.now())
                .build();

        couponRepository.save(build);

        return CouponsResponse.builder()
                .name(build.getName())
                .discount_rate(build.getDiscount_rate())
                .quantity(build.getQuantity())
                .build();
    }

    @Transactional
    public List<Coupon_issues> allGetCoupon(Long couponId) {

        if(couponRepository.existsById(couponId)){
            throw CouponErrorCode.ALREADY_ISSUED_COUPON.exception();
        }

        List<Users> allUsers = userRepository.findAll();

        List<Coupon_issues> issues = allUsers.stream()
                .map(user -> Coupon_issues.builder()
                        .couponId(couponId)
                        .userId(user.getId())
                        .is_used(false)
                        .build())
                .toList();

        List<Coupon_issues> couponIssues = couponIssuesRepository.saveAll(issues);

        return couponIssues;

    }

}
