package com.example.side_project.controller;

import com.example.side_project.domain.Coupons;
import com.example.side_project.dto.Coupon.*;
import com.example.side_project.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService service;

    /**
     * 쿠폰 리스트 ( 유저 )
     */
    @GetMapping
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
    public void getCoupon(@RequestBody CouponRequest request) {
        service.getCoupon(request);
        /**
         * 이벤트 쿠폰 받기 버튼 클릭 시 생기는 이벤트
         */
    }

    /**
     * 쿠폰 발급 ( 어드민 )
     */
    @PostMapping("/api/issue")
    public void issuanceCoupon(@RequestBody CouponsRequest request) {
        service.issuanceCoupon(request);
        /**
         * 어드민 페이지에서 해당 쿠폰 발급
         */
    }

}
