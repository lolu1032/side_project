package com.example.sideProject.payment.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor()
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long productId;
    private Long couponIssuesId;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public Payment(Long id, Long userId, Long productId, Long couponIssuesId, BigDecimal originalAmount, BigDecimal discountAmount, BigDecimal finalAmount, PaymentStatus status) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.couponIssuesId = couponIssuesId;
        this.originalAmount = originalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.status = status;
    }

}
