package com.example.sideProject.copon.Controller;

import com.example.sideProject.copon.domain.Coupons;
import com.example.sideProject.copon.dto.Coupon.*;
import com.example.sideProject.copon.Service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Coupon_Row_lock", description = "쿠폰 API(row lock)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
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
    public Page<Coupons> couponList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return service.couponList(pageable);
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
    public String  allGetCoupon(@RequestParam Long couponId) {
        service.allGetCouponAsync(couponId);
        return "모든 유저에게 쿠폰이 발급 완료되었습니다.";
        /**
         * 모든 유저에게 쿠폰을 발급
         */
    }

    /**
     * 쿠폰 사용 로직
     */
    @Operation(summary = "쿠폰 사용", description = "쿠폰을 사용합니다.")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "409", description = "이미 사용된 쿠폰입니다."),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/use")
    public ResponseEntity<com.example.sideProject.copon.dto.ApiResponse<?>> useCoupon(@RequestBody CouponIssuesRequest request) {
        /**
         * 유저 쿠폰 사용
         */
        try {
            service.useCoupon(request);
            return ResponseEntity.ok(com.example.sideProject.copon.dto.ApiResponse.success("쿠폰 사용 완료",null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(com.example.sideProject.copon.dto.ApiResponse.error(e.getMessage()));
        }

    }

}
