package com.example.side_project.repository;

import com.example.side_project.domain.CouponIssues;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssuesRepository extends JpaRepository<CouponIssues, Long> {
    CouponIssues findByUserId(Long aLong);
    CouponIssues findByCouponId(Long aLong);

    boolean existsByCouponIdAndUserId(Long id, Long aLong);
}
