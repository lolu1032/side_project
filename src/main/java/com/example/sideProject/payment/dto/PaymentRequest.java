package com.example.sideProject.payment.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRequest {
    private Long couponId;
    private Long userId;
    private Integer money;
    private Long productId;
}