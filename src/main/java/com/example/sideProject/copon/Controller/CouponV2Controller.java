package com.example.sideProject.copon.Controller;

import com.example.sideProject.copon.Service.CouponV2RedisService;
import com.example.sideProject.copon.dto.Coupon;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Coupon_Redis", description = "쿠폰 API (Redis)")
public class CouponV2Controller {
    private final CouponV2RedisService couponV2RedisService;

    @PostMapping("/v2/issue")
    public ResponseEntity<Map<String, String>> issue(@RequestBody Coupon.CouponIssueRequest request) {
        boolean success = couponV2RedisService.issue(request);

        if (success) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "쿠폰 발급 완료."));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "쿠폰 재고가 소진되었습니다."));
        }
    }

}
