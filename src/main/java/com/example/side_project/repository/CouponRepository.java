package com.example.side_project.repository;

import com.example.side_project.domain.coupons;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<coupons,Long> {
}
