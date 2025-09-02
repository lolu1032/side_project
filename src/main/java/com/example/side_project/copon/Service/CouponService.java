package com.example.side_project.copon.Service;

import com.example.side_project.copon.domain.CouponIssues;
import com.example.side_project.copon.domain.Coupons;
import com.example.side_project.user.domain.Users;
import com.example.side_project.copon.dto.Coupon.*;
import com.example.side_project.exception.CouponErrorCode;
import com.example.side_project.copon.repository.CouponIssuesRepository;
import com.example.side_project.copon.repository.CouponRepository;
import com.example.side_project.user.repository.UserRepository;
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

    private final Queue<CouponIssues> buffer = new ConcurrentLinkedQueue<>();
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
        List<CouponIssues> toSave = new ArrayList<>();
        while (!buffer.isEmpty()) {
            toSave.add(buffer.poll());
        }

        if (!toSave.isEmpty()) {
            couponIssuesRepository.saveAll(toSave);
        }
    }

    public void enqueue(CouponIssues issue) {
        buffer.add(issue);
    }

    @Transactional
    public CouponIssueResponse getCoupon(CouponRequest request) {
        Coupons coupon = couponRepository.findById(request.id())
                .orElseThrow(() -> CouponErrorCode.NOT_FOUND_COUPON.exception());

        boolean alreadyIssued = couponIssuesRepository.existsByCouponIdAndUserId(request.id(), request.userId());

        if (alreadyIssued) {
            throw CouponErrorCode.ISSUED_COUPON.exception();
        }

        int updated = couponRepository.decreaseQuantitySafely(request.id());

        if (updated == 0) {
            throw CouponErrorCode.SOLD_OUT_COUPON.exception();
        }

        CouponIssues issue = CouponIssues.builder()
                .couponId(request.id())
                .userId(request.userId())
                .isUsed(false)
                .build();

        enqueue(issue);

        return CouponIssueResponse.builder()
                .name(coupon.getName())
                .discountRate(coupon.getDiscountRate())
                .build();
    }

    public CouponsResponse issuanceCoupon(CouponsRequest request) {
        Coupons build = Coupons.builder()
                .name(request.name())
                .discountRate(request.discount_rate())
                .quantity(request.quantity())
                .startsAt(Instant.now())
                .build();

        couponRepository.save(build);

        return CouponsResponse.builder()
                .name(build.getName())
                .discountRate(build.getDiscountRate())
                .quantity(build.getQuantity())
                .build();
    }

    @Transactional
    public List<CouponIssues> allGetCoupon(Long couponId) {

        if(couponRepository.existsById(couponId)){
            throw CouponErrorCode.ALREADY_ISSUED_COUPON.exception();
        }

        List<Users> allUsers = userRepository.findAll();

        List<CouponIssues> issues = allUsers.stream()
                .map(user -> CouponIssues.builder()
                        .couponId(couponId)
                        .userId(user.getId())
                        .isUsed(false)
                        .build())
                .toList();

        List<CouponIssues> couponIssues = couponIssuesRepository.saveAll(issues);

        return couponIssues;

    }

    public void useCoupon(CouponIssuesRequest request) {
        CouponIssues byUserId = couponIssuesRepository.findByUserId(request.userId());
        CouponIssues byCouponId = couponIssuesRepository.findByCouponId(request.couponId());

        if(byUserId == null || byCouponId == null) {
            throw new IllegalArgumentException("쿠폰 또는 유저가 존재하지않습니다.");
        }

        if(byUserId.isUsed() == true) {
            throw new IllegalArgumentException("이미 사용한 쿠폰입니다.");
        }else if(byUserId.getExpiredAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("이미 만료된 쿠폰입니다.");
        }

        CouponIssues.builder()
                .couponId(byCouponId.getCouponId())
                .userId(byUserId.getUserId())
                .isUsed(true)
                .build();
    }
}
