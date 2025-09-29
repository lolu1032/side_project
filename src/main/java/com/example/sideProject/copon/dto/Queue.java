package com.example.sideProject.copon.dto;

import lombok.Builder;

public final class Queue {
    @Builder
    public record CouponQueueStatusResponse(
            long stock,
            long queueSize,
            long dbCount
    ) {}
    @Builder
    public record QueueJoinRequest(
            Long promotionId,
            Long userId
    ) {}
    @Builder
    public record QueuePosition(
            long position,
            String status
    ) {}
    @Builder
    public record QueueStatus(
            String status,
            long position,
            long totalWaiting,
            String message
    ) {}
    @Builder
    public record InitStockRequest(
            Long promotionId,
            int stock
    ) {}
    @Builder
    public record QueueInfo(
            Long promotionId,
            int currentStock,
            long queueSize,
            long processingCount
    ) {}
}
