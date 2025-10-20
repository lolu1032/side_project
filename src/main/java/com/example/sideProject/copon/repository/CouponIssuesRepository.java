package com.example.sideProject.copon.repository;

import com.example.sideProject.copon.domain.CouponIssues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponIssuesRepository extends JpaRepository<CouponIssues, Long> {
    boolean existsByCouponIdAndUserId(Long id, Long aLong);

    List<CouponIssues> findByUserId(Long id);
}
