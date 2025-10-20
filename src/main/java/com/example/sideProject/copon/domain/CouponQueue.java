package com.example.sideProject.copon.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long couponId;

    private Long userId;

    private String status = "PENDING";

    private Instant createdAt = Instant.now();

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.status == null) {
            this.status = "PENDING";
        }
    }

    public CouponQueue withStatus(String newStatus) {
        return CouponQueue.builder()
                .id(this.id)
                .couponId(this.couponId)
                .userId(this.userId)
                .status(newStatus)
                .createdAt(this.createdAt)
                .build();
    }
}
