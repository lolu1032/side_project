package com.example.sideProject.payment.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long productId;
    private int originalAmount;
    private int discountAmount;
    private int charge;
    private int finalAmount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}