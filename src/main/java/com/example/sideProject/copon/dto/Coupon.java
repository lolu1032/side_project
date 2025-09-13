package com.example.sideProject.copon.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public final class Coupon {

    @Builder
    public record CouponRequest(
            Long id,
            Long userId
    ){
    }

    @Builder
    public record CouponsRequest(
            @NotBlank(message = "이름은 필수 입력입니다.")
            String name,
            @Min(value = 1 , message = "수량은 최소 1개 이상이어야합니다.")
            int quantity,
            @Min(value = 10, message = "할인률은 10퍼 이상이어야 합니다.")
            int discount_rate
    ){
    }

    @Builder
    public record CouponIssuesRequest(
            Long userId,
            Long couponId
    ){}

    @Builder
    public record CouponsResponse(
            String name,
            int quantity,
            int discountRate
    ){}

    @Builder
    public record CouponIssueResponse(
            String name,
            int discountRate
    ) {}

    @Builder
    public record CouponIssueRequest(
       Long promotionId,
       Long userId
    ){}
}
