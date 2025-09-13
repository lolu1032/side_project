package com.example.sideProject.copon.repository;

import com.example.sideProject.copon.domain.CouponIssues;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssuesRepository extends JpaRepository<CouponIssues, Long> {
    CouponIssues findByUserId(Long aLong);
    CouponIssues findByCouponId(Long aLong);

    boolean existsByCouponIdAndUserId(Long id, Long aLong);
}
