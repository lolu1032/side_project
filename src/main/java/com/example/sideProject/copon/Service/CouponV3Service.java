package com.example.sideProject.copon.Service;

import com.example.sideProject.copon.dto.Coupon;
import com.example.sideProject.copon.repository.CouponRepository;
import com.example.sideProject.copon.repository.PromotionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponV3Service implements CouponStrategy {
    private final PromotionRepository promotionRepository;
    private final CouponRepository couponRepository;
    //얘는 결국에 Cache임. 즉 빠르게 조회하기 위함.
    //하지만 단일서버 Cache이기 때문에 Scale-Out 상황에선 Cache 공유가 안되기 때문에 정상 동작 안함.
    //그러면 2가지 해결책이 있음.
    // 1. Cache 동기화 (매번 모든 인스턴스 서버에 이 HashMap 안에 있는 값을 동기화하기 (이건 또 동시성 문제가 발생할 수 있고, 또 방지하려면 Lock이 필요함 => 즉 처리량 감소로 이어짐 => 그럼 굳이?)
    // 2. Cache 전용 서버 만들기
    //       => 이게 즉 레디스 서버를 쓰는 이유임.


    // 캐싱 전용 서버 만들기로 해결되나요

    private static final HashMap<Long, Integer> stockMap = new HashMap<>();

    static {
        stockMap.put(1L, 100);
    }

//    public void issue(Long userId, Long promotionId) {
//        // 여기서 Lock을 거는게 문제?
////        int updateRows = promotionRepository.decreaseStock(promotionId);
////        if (updateRows == 0) {
////            throw new IllegalStateException("쿠폰 재고가 소진되었습니다,");
////        }
//        decreaseStock(promotionId);
//
//        var promotion = promotionRepository.findById(promotionId).get();
//        if (!promotion.isActive()) {
//            throw new IllegalStateException("프로모션 기간이 아닙니다.");
//        }
//        var issuedCoupon = Coupon.issued(promotionId, userId);
//        couponRepository.save(issuedCoupon);
//    }
    @Override
    public void issue(Coupon.CouponIssueRequest request) {
        decreaseStock(request.promotionId());

        var promotion = promotionRepository.findById(request.promotionId()).get();
        if (!promotion.isActive()) {
            throw new IllegalStateException("프로모션 기간이 아닙니다.");
        }

        var issuedCoupon = com.example.sideProject.copon.domain.Coupon.issued(request.promotionId(), request.userId());
        couponRepository.save(issuedCoupon);
    }

    @Override
    public String getType() {
        return "synchronized";
    }

    // Lock을 겁니다. 자바 모니터 락임
    // 이게 결국 레디스로 하는 일임. 자바로도 똑같이 구현 가능. (Why? -> 인메모리 디비를 레디스랑 똑같이 구현했음)
    // => 그렇다면 굳이 레디스를 쓰는 이유는?
    private synchronized void decreaseStock(Long promotionId) {
        int stock = stockMap.get(promotionId);
        if (stock == 0) {
            throw new IllegalStateException("쿠폰 재고가 소진되었습니다,");
        }
        stockMap.put(promotionId, stock - 1);
    }

}
