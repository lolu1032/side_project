package com.example.sideProject.payment.controller;

import com.example.sideProject.payment.dto.PaymentRequest;
import com.example.sideProject.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payment")
    public void payment(@RequestBody PaymentRequest paymentRequest) {
        paymentService.payment(paymentRequest);
        /**
         * 결제 - 쿠폰 사용 / 미사용
         */
    }
}
