package com.example.side_project.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int quantity;
    private int discountRate;
    private Instant startsAt;

    public void decreaseQuantity() {
        if (this.quantity <= 0) {
            throw new IllegalArgumentException("쿠폰 수량이 부족합니다.");
        }
        this.quantity--;
    }
}
