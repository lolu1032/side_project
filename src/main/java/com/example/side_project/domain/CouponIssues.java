package com.example.side_project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Coupon_issues")
public class CouponIssues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private Instant issuedAt;
    private boolean isUsed;
    private Instant expiredAt;

    private Long userId;
    private Long couponId;

    @PrePersist
    public void prePersist() {
        if (this.uuid == null || this.uuid.isBlank()) {
            this.uuid = UUID.randomUUID().toString();
        }
        if (this.issuedAt == null) {
            // 현재 날짜 기준으로 자정(00:00:00) 시각 생성
            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            this.issuedAt = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        }

        if (this.expiredAt == null) {
            // issued_at 기준으로 7일 뒤 23:59:59 세팅
            LocalDate issuedDate = this.issuedAt.atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate expiredDate = issuedDate.plusDays(7);
            LocalDateTime expiredDateTime = expiredDate.atTime(LocalTime.MAX); // 23:59:59.999999999

            this.expiredAt = expiredDateTime.atZone(ZoneId.systemDefault()).toInstant();
        }
    }

}
