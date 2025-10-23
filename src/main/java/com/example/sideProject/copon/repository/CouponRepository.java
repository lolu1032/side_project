package com.example.sideProject.copon.repository;

import com.example.sideProject.copon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon,Long> {
    boolean existsByUserIdAndPromotionId(Long userId, Long promotionId);
}
