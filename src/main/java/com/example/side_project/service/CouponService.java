package com.example.side_project.service;

import com.example.side_project.domain.Coupon_issues;
import com.example.side_project.domain.Coupons;
import com.example.side_project.dto.Coupon.*;
import com.example.side_project.repository.CouponIssuesRepository;
import com.example.side_project.repository.CouponRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssuesRepository couponIssuesRepository;
//
//    private final BlockingQueue<CouponRequest> queue = new LinkedBlockingQueue<>();
//
//    private final AtomicInteger issuedCount = new AtomicInteger(0);

//    public String handleRequest(CouponRequest request) {
//        queue.offer(request);
//
//        // 현재 몇 번째 요청인지 확인
//        int position = issuedCount.incrementAndGet();
//
//        if (position <= 100) {
//            return "SUCCESS_PENDING"; // 곧 발급될 예정
//        } else {
//            return "FAILED"; // 수량 초과
//        }
//    }

//    // 발급 consumer: 진짜 DB insert는 여기서
//    @PostConstruct
//    public void startConsumer() {
//        Executors.newSingleThreadExecutor().submit(() -> {
//            while (true) {
//                try {
//                    CouponRequest request = queue.take();
//
//                    if (issuedCount.get() <= 100) {
//                        processCoupon(request);
//                    }
//                    // else는 아무것도 안 함 (이미 응답에서 실패 처리됨)
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

//    @Transactional
//    public void processCoupon(CouponRequest request) {
//        couponRepository.decreaseQuantitySafely(request.id()); // UPDATE ... WHERE quantity > 0
//        couponIssuesRepository.save(
//                Coupon_issues.builder()
//                        .couponId(request.id())
//                        .userId(request.userId())
//                        .is_used(false)
//                        .build()
//        );
//    }

    public List<Coupons> couponList() {

        List<Coupons> all = couponRepository.findAll();

        if(all.isEmpty()) {
            throw new IllegalArgumentException("쿠폰이 존재하지않습니다.");
        }

        return all;
    }



    @Transactional
    public void getCoupon(CouponRequest request) {

        Coupons coupon = couponRepository.findByForUpdate(request.id())
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        if (coupon.getQuantity() <= 0) {
            throw new IllegalArgumentException("쿠폰이 모두 소진됐습니다.");
        }

        coupon.decreaseQuantity();

        // 4. 쿠폰 발급 기록 저장
        Coupon_issues couponIssue = Coupon_issues.builder()
                .couponId(request.id())
                .is_used(false)
                .userId(request.userId())
                .build();

        couponIssuesRepository.save(couponIssue);

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

}
