package com.example.side_project.repository;

import com.example.side_project.domain.Coupon_issues;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssuesRepository extends JpaRepository<Coupon_issues, Long> {
    Coupon_issues findByUserId(Long aLong);
    Coupon_issues findByCouponId(Long aLong);
}
