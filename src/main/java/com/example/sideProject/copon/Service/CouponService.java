package com.example.sideProject.copon.Service;

import com.example.sideProject.copon.domain.CouponIssues;
import com.example.sideProject.copon.domain.CouponQueue;
import com.example.sideProject.copon.domain.Coupons;
import com.example.sideProject.copon.repository.CouponQueueRepository;
import com.example.sideProject.user.domain.Users;
import com.example.sideProject.copon.dto.Coupon.*;
import com.example.sideProject.exception.CouponErrorCode;
import com.example.sideProject.copon.repository.CouponIssuesRepository;
import com.example.sideProject.copon.repository.CouponsRepository;
import com.example.sideProject.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponsRepository couponsRepository;
    private final CouponIssuesRepository couponIssuesRepository;
    private final UserRepository userRepository;
    private final CouponQueueRepository couponQueueRepository;


    @Scheduled(fixedRate = 3000)
    public void flushToDB() {
        List<CouponQueue> pendingList = couponQueueRepository.findTop10ByStatusOrderByCreatedAtAsc("PENDING");

        for (CouponQueue queue : pendingList) {
            processQueue(queue);
        }
    }

    @Transactional
    public void processQueue(CouponQueue queue) {
        try {
            couponQueueRepository.save(queue.withStatus("PROCESSING"));

            CouponIssues issue = CouponIssues.builder()
                    .couponId(queue.getCouponId())
                    .userId(queue.getUserId())
                    .isUsed(false)
                    .build();
            couponIssuesRepository.save(issue);

            couponQueueRepository.save(queue.withStatus("DONE"));

        } catch (Exception e) {
            couponQueueRepository.save(queue.withStatus("PENDING"));
        }
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

        CouponQueue queue = CouponQueue.builder()
                .couponId(request.id())
                .userId(request.userId())
                .status("PENDING")
                .build();

        couponQueueRepository.save(queue);

        return CouponIssueResponse.builder()
                .name(coupon.getName())
                .discountRate(coupon.getDiscountRate())
                .build();
    }

    public Page<Coupons> couponList(Pageable pageable) {
        Page<Coupons> page = couponsRepository.findAll(pageable);

        if (page.isEmpty()) {
            throw CouponErrorCode.NOT_FOUND_COUPON.exception();
        }

        return page;
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

    @Async
    public void allGetCouponAsync(Long couponId) {
        if(!couponsRepository.existsById(couponId)) {
            throw CouponErrorCode.NOT_FOUND_COUPON.exception();
        }

        int pageNumber = 0;
        int pageSize = 500;
        Page<Users> userPage;

        do {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            userPage = userRepository.findAll(pageable);

            saveCouponPage(userPage.getContent(), couponId);

            pageNumber++;
        } while(userPage.hasNext());
    }

    /**
     * 페이지 단위로 쿠폰 발급 (트랜잭션 분리)
     */
    @Transactional
    public void saveCouponPage(List<Users> users, Long couponId) {
        List<CouponIssues> issues = users.stream()
                .map(user -> CouponIssues.builder()
                        .couponId(couponId)
                        .userId(user.getId())
                        .isUsed(false)
                        .build())
                .collect(Collectors.toList());

        couponIssuesRepository.saveAll(issues);
    }

    public void useCoupon(CouponIssuesRequest request) {
        List<CouponIssues> coupons = couponIssuesRepository.findByUserId(request.userId());

        CouponIssues couponToUse = coupons.stream()
                .filter(c -> !c.isUsed() && c.getExpiredAt().isAfter(Instant.now()))
                .findFirst()
                .orElseThrow(() -> CouponErrorCode.NOT_FOUND_COUPON_OR_USER.exception());

        CouponIssues usedCoupon = CouponIssues.builder()
                .id(couponToUse.getId())
                .couponId(couponToUse.getCouponId())
                .userId(couponToUse.getUserId())
                .isUsed(true)
                .expiredAt(couponToUse.getExpiredAt())
                .build();

        couponIssuesRepository.save(usedCoupon);
    }
}
