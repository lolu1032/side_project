package com.example.sideProject.copon.Controller;

import com.example.sideProject.copon.Service.CouponV3Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Coupon_V3", description = "쿠폰 V3 API(synchronized)")
public class CouponV3Controller {
    private final CouponV3Service couponV3Service;

    @PostMapping("/issue")
    public Map<String, Object> issue(@RequestBody Map<String, Long> param) {
        try {
            couponV3Service.issue(param.get("userId"), param.get("promotionId"));
            return Map.of("success", true, "message", "쿠폰 발급 완료");
        } catch (IllegalStateException e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }

}
