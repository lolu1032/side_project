package com.example.sideProject.copon.Controller;

import com.example.sideProject.copon.Service.CouponQueueService;
import com.example.sideProject.copon.constant.QueueStateType;
import com.example.sideProject.copon.dto.ApiResponse;
import com.example.sideProject.copon.dto.Queue.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Coupon Queue", description = "쿠폰 대기열 API")
public class CouponQueueController {
    private final CouponQueueService couponQueueService;

    @Operation(summary = "대기열 참여", description = "사용자를 쿠폰 발급 대기열에 추가합니다.")
    @PostMapping("/queue/join")
    public ResponseEntity<ApiResponse<?>> joinQueue(@RequestBody QueueJoinRequest request) {
        try {
            QueuePosition position = couponQueueService.addToQueue(request.promotionId(), request.userId());

            String message = switch (position.status()) {
                case QueueStateType.WAITING -> String.format(QueueStateType.WAITING.getMessage(), position.position());
                case QueueStateType.PROCESSING -> QueueStateType.PROCESSING.getMessage();
                case QueueStateType.COMPLETED -> QueueStateType.COMPLETED.getMessage();
                case QueueStateType.FAILED -> QueueStateType.FAILED.getMessage();
                default -> QueueStateType.DEFAULT.getMessage();
            };

            return ResponseEntity.ok(ApiResponse.success(message,position));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("대기열 추가 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "대기열 상태 조회", description = "사용자의 현재 대기열 상태를 조회합니다.")
    @GetMapping("/queue/status")
    public ResponseEntity<ApiResponse<?>> getQueueStatus(@RequestParam Long promotionId, @RequestParam Long userId) {
        try {
            QueueStatus status = couponQueueService.getQueueStatus(promotionId, userId);
            return ResponseEntity.ok(ApiResponse.success("상태 조회 성공", status));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("상태 조회 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "재고 초기화", description = "프로모션의 쿠폰 재고를 초기화합니다.")
    @PostMapping("/queue/init-stock")
    public ResponseEntity<ApiResponse<?>> initStock(@RequestBody InitStockRequest request) {
        try {
            couponQueueService.initStock(request.promotionId(), request.stock());
            String message = String.format("프로모션 %d의 재고가 %d개로 초기화되었습니다.",
                    request.promotionId(), request.stock());
            return ResponseEntity.ok(ApiResponse.success(message, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("재고 초기화 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "큐 정보 조회", description = "프로모션의 전체 큐 정보를 조회합니다.")
    @GetMapping("/queue/info")
    public ResponseEntity<?> getQueueInfo(@RequestParam Long promotionId) {
        try {
            QueueInfo info = QueueInfo.builder()
                    .promotionId(promotionId)
                    .currentStock(couponQueueService.getCurrentStock(promotionId))
                    .queueSize(couponQueueService.getQueueLength(promotionId))
                    .processingCount(couponQueueService.getProcessingCount(promotionId))
                    .build();

            return ResponseEntity.ok(ApiResponse.success("큐 정보 조회 성공", info));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("큐 정보 조회 중 오류가 발생했습니다."));
        }
    }
}