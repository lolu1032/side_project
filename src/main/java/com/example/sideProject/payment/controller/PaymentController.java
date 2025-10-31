package com.example.sideProject.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {


    @PostMapping
    public void payment() {
        /**
         * 결제 - 쿠폰 사용 / 미사용
         */
    }
}
