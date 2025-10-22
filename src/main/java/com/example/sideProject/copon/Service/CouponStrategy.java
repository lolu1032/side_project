package com.example.sideProject.copon.Service;

import com.example.sideProject.copon.dto.Coupon;

public interface CouponStrategy {
    boolean issue(Coupon.CouponIssueRequest request);
    String getName();
}
