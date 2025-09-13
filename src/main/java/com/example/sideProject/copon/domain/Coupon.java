package com.example.sideProject.copon.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private Long promotionId;
    private Long userId;
    private boolean isUsed;
    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    public Coupon(Long promotionId, Long userId, LocalDateTime createdAt) {
        this.promotionId = promotionId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public static Coupon issued(Long promotionId, Long userId) {
        var now = LocalDateTime.now();
        return Coupon.builder()
                .promotionId(promotionId)
                .userId(userId)
                .createdAt(now)
                .build();
    }
}
