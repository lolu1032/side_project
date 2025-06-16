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
public class Coupon_issues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private Instant issued_at;
    private boolean is_used;
    private Instant expired_at;

    private Long userId;
    private Long couponId;

    @PrePersist
    public void prePersist() {
        if (this.uuid == null || this.uuid.isBlank()) {
            this.uuid = UUID.randomUUID().toString();
        }
        if (this.issued_at == null) {
            // 현재 날짜 기준으로 자정(00:00:00) 시각 생성
            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            this.issued_at = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        }

        if (this.expired_at == null) {
            // issued_at 기준으로 7일 뒤 23:59:59 세팅
            LocalDate issuedDate = this.issued_at.atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate expiredDate = issuedDate.plusDays(7);
            LocalDateTime expiredDateTime = expiredDate.atTime(LocalTime.MAX); // 23:59:59.999999999

            this.expired_at = expiredDateTime.atZone(ZoneId.systemDefault()).toInstant();
        }
    }

}
