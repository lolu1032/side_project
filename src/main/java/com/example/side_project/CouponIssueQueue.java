//package com.example.side_project;
//
//import com.example.side_project.domain.Coupon_issues;
//import com.example.side_project.dto.Coupon;
//import com.example.side_project.repository.CouponIssuesRepository;
//import com.example.side_project.repository.CouponRepository;
//import jakarta.annotation.PostConstruct;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.Executors;
//import java.util.concurrent.LinkedBlockingQueue;
//
//@Component
//@RequiredArgsConstructor
//public class CouponIssueQueue {
//
//    private final BlockingQueue<Coupon.CouponRequest> queue = new LinkedBlockingQueue<>();
//    private final CouponRepository couponRepository;
//    private final CouponIssuesRepository couponIssuesRepository;
//
//    public void enqueue(Coupon.CouponRequest request ) {
//        queue.offer(request);
//    }
//
//    @PostConstruct
//    public void startConsumer() {
//        Executors.newSingleThreadExecutor().submit(() -> {
//            while (true) {
//                try {
//                    Coupon.CouponRequest request = queue.take(); // 큐에서 꺼냄 (FIFO)
//                    processRequest(request);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    @Transactional
//    public void processRequest(Coupon.CouponRequest request) {
//        int updated = couponRepository.decreaseQuantitySafely(request.id());
//        if (updated == 0) {
//            // 수량 소진
//            return;
//        }
//
//        try {
//            Coupon_issues issue = Coupon_issues.builder()
//                    .couponId(request.id())
//                    .userId(request.userId())
//                    .is_used(false)
//                    .build();
//            couponIssuesRepository.save(issue);
//        } catch (DataIntegrityViolationException e) {
//            // 중복 발급 무시
//        }
//    }
//}
