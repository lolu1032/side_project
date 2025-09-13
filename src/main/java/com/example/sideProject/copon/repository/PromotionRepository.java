package com.example.sideProject.copon.repository;

import com.example.sideProject.copon.domain.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    @Modifying
    @Query("UPDATE Promotion p SET p.stock = p.stock - 1 WHERE p.id = :id AND p.stock > 0")
    int decreaseStock(@Param("id") Long id);
}
