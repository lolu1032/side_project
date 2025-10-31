package com.example.sideProject.payment.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long couponId;
    private Long userId;
    private Integer money;
    private Long productId;
}