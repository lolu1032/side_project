package com.example.sideProject.copon.Service;

import com.example.sideProject.copon.domain.CouponIssues;
import com.example.sideProject.copon.domain.Coupons;
import com.example.sideProject.user.domain.Users;
import com.example.sideProject.copon.dto.Coupon.*;
import com.example.sideProject.exception.CouponErrorCode;
import com.example.sideProject.copon.repository.CouponIssuesRepository;
import com.example.sideProject.copon.repository.CouponsRepository;
import com.example.sideProject.user.repository.UserRepository;
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
    private final CouponsRepository couponsRepository;
    private final CouponIssuesRepository couponIssuesRepository;
    private final UserRepository userRepository;

    public List<Coupons> couponList() {

        List<Coupons> all = couponsRepository.findAll();

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
        Coupons coupon = couponsRepository.findById(request.id())
                .orElseThrow(() -> CouponErrorCode.NOT_FOUND_COUPON.exception());

        boolean alreadyIssued = couponIssuesRepository.existsByCouponIdAndUserId(request.id(), request.userId());

        if (alreadyIssued) {
            throw CouponErrorCode.ISSUED_COUPON.exception();
        }

        int updated = couponsRepository.decreaseQuantitySafely(request.id());

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

        couponsRepository.save(build);

        return CouponsResponse.builder()
                .name(build.getName())
                .discountRate(build.getDiscountRate())
                .quantity(build.getQuantity())
                .build();
    }

    @Transactional
    public List<CouponIssues> allGetCoupon(Long couponId) {

        if(couponsRepository.existsById(couponId)){
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
        CouponIssues getUserIdOrNullIfNotFound = couponIssuesRepository.findByUserId(request.userId());
        CouponIssues getCouponIdOrNullIfNotFound = couponIssuesRepository.findByCouponId(request.couponId());

        if(getUserIdOrNullIfNotFound == null || getCouponIdOrNullIfNotFound == null) {
//            throw new IllegalArgumentException("쿠폰 또는 유저가 존재하지않습니다.");
            throw CouponErrorCode.NOT_FOUND_COUPON_OR_USER.exception();
        }

        if(getUserIdOrNullIfNotFound.isUsed() == true) {
            throw CouponErrorCode.USE_COUPON.exception();
        }else if(getUserIdOrNullIfNotFound.getExpiredAt().isBefore(Instant.now())) {
            throw CouponErrorCode.EXPIRED_COUPON.exception();
        }

        CouponIssues.builder()
                .couponId(getCouponIdOrNullIfNotFound.getCouponId())
                .userId(getUserIdOrNullIfNotFound.getUserId())
                .isUsed(true)
                .build();
    }
}
