package com.example.side_project.copon.Controller;

import com.example.side_project.copon.Service.CouponV2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CouponV2Controller {
    private final CouponV2Service couponV2Service;

    @PostMapping("/{couponId}/issue")
    public String issueCoupon(
            @PathVariable Long couponId,
            @RequestParam Long userId
    ) {
        return couponV2Service.issueCoupon(couponId, userId);
    }
}
