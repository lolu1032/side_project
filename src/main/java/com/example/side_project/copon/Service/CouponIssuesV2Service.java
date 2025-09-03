package com.example.side_project.copon.Service;

import com.example.side_project.copon.domain.CouponIssues;
import com.example.side_project.copon.repository.CouponIssuesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CouponIssuesV2Service {
    private final CouponIssuesRepository couponIssuesRepository;

    // 비동기 처리 (taskExecutor -> 비동기명)
    @Async("taskExecutor")
    public void saveCouponIssue(Long couponId, Long userId) {
        CouponIssues issues = CouponIssues.builder()
                .couponId(couponId)
                .userId(userId)
                .isUsed(false)
                .issuedAt(Instant.now())
                .build();

        couponIssuesRepository.save(issues);
    }
}
