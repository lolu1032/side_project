package com.example.sideProject.copon.repository;

import com.example.sideProject.copon.domain.CouponQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CouponQueueRepository extends JpaRepository<CouponQueue,Long> {

    List<CouponQueue> findTop10ByStatusOrderByCreatedAtAsc(String status);

    @Modifying
    @Query("UPDATE CouponQueue q SET q.status = :status WHERE q.id = :id")
    void updateStatus(Long id, String status);
}
