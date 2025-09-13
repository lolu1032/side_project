package com.example.sideProject.copon.repository;

import com.example.sideProject.copon.domain.Coupons;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface CouponsRepository extends JpaRepository<Coupons,Long> {
    @Modifying
    @Query("UPDATE Coupons c SET c.quantity = c.quantity - 1 WHERE c.id = :id AND c.quantity > 0")
    int decreaseQuantitySafely(@Param("id") Long id);
}
