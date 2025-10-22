package com.example.sideProject.copon.Factory;

import com.example.sideProject.copon.Service.CouponStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponStrategyFactory {
    private final List<CouponStrategy> strategies;

    public CouponStrategy getStrategy(String type) {
        return strategies.stream()
                .filter(s -> s.getName().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 쿠폰 타입: " + type));
    }
}