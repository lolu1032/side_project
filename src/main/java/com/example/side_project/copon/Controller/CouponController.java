package com.example.side_project.copon.Controller;

import com.example.side_project.copon.domain.CouponIssues;
import com.example.side_project.copon.domain.Coupons;
import com.example.side_project.copon.dto.Coupon.*;
import com.example.side_project.copon.Service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Coupon_V1", description = "쿠폰 V1 API(비관적 락)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/apis")
public class CouponController {

    private final CouponService service;

    /**
     * 쿠폰 리스트 ( 유저 )
     */
    @Operation(summary = "쿠폰 목록", description = "쿠폰 목록을 출력합니다.")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "404", description = "쿠폰이 존재하지 않습니다."),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/list")
    public List<Coupons> couponList() {
        return service.couponList();
        /**
         * 이벤트 쿠폰 리스트가 보임
         */
    }

    /**
     * 쿠폰 받기 ( 유저 )
     */
    @Operation(summary = "쿠폰 받기", description = "쿠폰을 받습니다.")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "404", description = "쿠폰이 존재하지 않습니다."),
            @ApiResponse(responseCode = "400", description = "쿠폰이 모두 소진됐습니다."),
            @ApiResponse(responseCode = "409", description = "이미 모든 유저에게 발급한 쿠폰입니다."),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/get")
    public ResponseEntity<CouponIssueResponse> getCoupon(@RequestBody CouponRequest request) {
        CouponIssueResponse response = service.getCoupon(request);
        return ResponseEntity.ok(response);
        /**
         * 이벤트 쿠폰 받기 버튼 클릭 시 생기는 이벤트
         */
    }

    /**
     * 쿠폰 발급 ( 어드민 )
     */
    @Operation(summary = "쿠폰 발급(어드민)", description = "쿠폰을 발급합니다.")
    @PostMapping("/issue")
    public ResponseEntity<CouponsResponse> issuanceCoupon(@RequestBody CouponsRequest request) {
        CouponsResponse response = service.issuanceCoupon(request);
        return ResponseEntity.ok(response);
        /**
         * 어드민 페이지에서 해당 쿠폰 발급
         */
    }

    /**
     * 전체 쿠폰 발급
     */
    @Operation(summary = "쿠폰 발급", description = "전체 유저에게 쿠폰을 발급합니다.")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "409", description = "이미 발급된 쿠폰입니다."),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/allGet")
    public List<CouponIssues> allGetCoupon(@RequestParam Long couponId) {
        return service.allGetCoupon(couponId);
        /**
         * 모든 유저에게 쿠폰을 발급
         */
    }

    /**
     * 쿠폰 사용 로직
     */
    public void useCoupon(@RequestBody CouponIssuesRequest request) {
        /**
         * 유저 쿠폰 사용
         */
    }

}
