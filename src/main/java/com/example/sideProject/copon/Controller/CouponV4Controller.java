package com.example.sideProject.copon.Controller;

import com.example.sideProject.copon.Service.CouponV4Service;
import com.example.sideProject.copon.dto.Coupon.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Coupon_V4", description = "쿠폰 V4 API(Atomic)")
public class CouponV4Controller {
    private final CouponV4Service couponV4Service;

    @PostMapping("/v4/issue")
    public Map<String, Object> issue(@RequestBody CouponIssueRequest request) {
        try {
            couponV4Service.issue(request);
            return Map.of("success", true, "message", "쿠폰 발급 완료");
        } catch (IllegalStateException e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }
}
