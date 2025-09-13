package com.example.sideProject.copon.Controller;

import com.example.sideProject.copon.Service.CouponV2RedisService;
import com.example.sideProject.copon.dto.Coupon;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Coupon_V2", description = "쿠폰 V2 API (Redis)")
public class CouponV2Controller {
    private final CouponV2RedisService couponV2RedisService;

    @PostMapping("/v2/issue")
    public Map<String, Object> issue(@RequestBody Coupon.CouponIssueRequest request) {
        try {
            couponV2RedisService.issue(request);
            return Map.of("success", true, "message", "쿠폰 발급 완료");
        } catch (IllegalStateException e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }
}
