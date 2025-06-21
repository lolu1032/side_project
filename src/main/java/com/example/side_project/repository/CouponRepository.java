package com.example.side_project.repository;

import com.example.side_project.domain.Coupons;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRepository extends JpaRepository<Coupons,Long> {
    @Modifying
    @Query("UPDATE Coupons c SET c.quantity = c.quantity - 1 WHERE c.id = :id AND c.quantity > 0")
    int decreaseQuantitySafely(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("Select c from Coupons c where c.id = :id")
    Coupons findByForUpdate(@Param("id") Long id);
}
