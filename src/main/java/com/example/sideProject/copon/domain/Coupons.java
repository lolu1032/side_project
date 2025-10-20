package com.example.sideProject.copon.domain;

import jakarta.persistence.*;
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

    public void decreaseQuantity(int amount) {
        this.quantity = Math.max(0, this.quantity - amount);
    }
}
