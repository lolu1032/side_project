package com.example.side_project.controller;

import com.example.side_project.domain.Coupon_issues;
import com.example.side_project.domain.Coupons;
import com.example.side_project.dto.Coupon.*;
import com.example.side_project.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService service;

    /**
     * 쿠폰 리스트 ( 유저 )
     */
    @GetMapping("/list")
    public List<Coupons> couponList() {
        return service.couponList();
        /**
         * 이벤트 쿠폰 리스트가 보임
         */
    }

    /**
     * 쿠폰 받기 ( 유저 )
     */
    @PostMapping("/api/get")
    public ResponseEntity<CouponIssueResponse> getCoupon(@RequestBody CouponRequest request) {
        CouponIssueResponse response = service.getCoupon(request);
        return ResponseEntity.ok(response);
        /**
         * 이벤트 쿠폰 받기 버튼 클릭 시 생기는 이벤트
         */
    }

    /**
     * 쿠폰 발급 ( 어드민 )
     */
    @PostMapping("/api/issue")
    public ResponseEntity<CouponsResponse> issuanceCoupon(@RequestBody CouponsRequest request) {
        CouponsResponse response = service.issuanceCoupon(request);
        return ResponseEntity.ok(response);
        /**
         * 어드민 페이지에서 해당 쿠폰 발급
         */
    }

    /**
     * 전체 쿠폰 발급
     */

    @PostMapping("/api/allGet")
    public List<Coupon_issues> allGetCoupon(@RequestParam Long couponId) {
        return service.allGetCoupon(couponId);
        /**
         * 모든 유저에게 쿠폰을 발급
         */
    }

    /**
     * 쿠폰 사용 로직
     */
    public void useCoupon(@RequestBody CouponIssuesRequest request) {
        /**
         * 유저 쿠폰 사용
         */
    }

}
