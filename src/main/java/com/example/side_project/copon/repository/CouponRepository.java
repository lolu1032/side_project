package com.example.side_project.copon.repository;

import com.example.side_project.copon.domain.Coupons;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface CouponRepository extends JpaRepository<Coupons,Long> {
    @Modifying
    @Query("UPDATE Coupons c SET c.quantity = c.quantity - 1 WHERE c.id = :id AND c.quantity > 0")
    int decreaseQuantitySafely(@Param("id") Long id);
}
