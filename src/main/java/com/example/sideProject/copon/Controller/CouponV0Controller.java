package com.example.sideProject.copon.Controller;

import com.example.sideProject.copon.Factory.CouponStrategyFactory;
import com.example.sideProject.copon.Service.CouponStrategy;
import com.example.sideProject.copon.dto.Coupon;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
@Tag(name = "Coupon", description = "쿠폰 통합 API")
public class CouponV0Controller {

    private final CouponStrategyFactory strategyFactory;

    @PostMapping("/issue")
    public ResponseEntity<Map<String, Object>> issue(
            @RequestParam String type,
            @RequestBody Coupon.CouponIssueRequest request) {

        try {
            CouponStrategy strategy = strategyFactory.getStrategy(type);
            strategy.issue(request);
            return ResponseEntity.ok(Map.of("success", true, "message", "쿠폰 발급 완료 (" + type + ")"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}