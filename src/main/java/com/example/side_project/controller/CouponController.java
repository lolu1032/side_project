package com.example.side_project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponController {

    /**
     * 쿠폰 리스트 ( 유저 )
     */
    @GetMapping
    public void couponList() {
        /**
         * 이벤트 쿠폰 리스트가 보임
         */
    }

    /**
     * 쿠폰 받기 ( 유저 )
     */
    @PostMapping("/api/get")
    public void getCoupon() {
        /**
         * 이벤트 쿠폰 받기 버튼 클릭 시 생기는 이벤트
         */
    }

    /**
     * 쿠폰 발급 ( 어드민 )
     */
    @PostMapping("/api/issue")
    public void issuanceCoupon() {
        /**
         * 어드민 페이지에서 해당 쿠폰 발급
         */
    }

}
