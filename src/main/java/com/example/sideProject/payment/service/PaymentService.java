package com.example.sideProject.payment.service;

import com.example.sideProject.copon.domain.Coupon;
import com.example.sideProject.copon.domain.Promotion;
import com.example.sideProject.copon.repository.CouponRepository;
import com.example.sideProject.copon.repository.PromotionRepository;
import com.example.sideProject.payment.domain.Payment;
import com.example.sideProject.payment.domain.PaymentStatus;
import com.example.sideProject.payment.domain.Product;
import com.example.sideProject.payment.dto.PaymentRequest;
import com.example.sideProject.payment.repository.PaymentRepository;
import com.example.sideProject.payment.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final CouponRepository couponRepository;
    private final PromotionRepository promotionRepository;

    public void payment(PaymentRequest paymentRequest) {
        /**
         * 쿠폰 사용했냐 안했냐 구분
         * 결제 로직 구현
         */
        Product product = productRepository.findById(paymentRequest.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품이 없다요"));

        Coupon coupon = null;
        Promotion promotion = null;
        if (paymentRequest.getCouponId() != null && couponRepository.existsById(paymentRequest.getCouponId())) {
            coupon = couponRepository.findById(paymentRequest.getCouponId())
                    .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

            if (coupon.getPromotionId() != null) {
                promotion = promotionRepository.findById(coupon.getPromotionId())
                        .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않습니다."));
            }
        }
        int discountPercent = 0;
        int discountAmount = 0;
        int finalPrice = product.getPrice();

        if (promotion != null) {
            discountPercent = promotion.getDiscount();
            discountAmount = product.getPrice() * discountPercent / 100;
            finalPrice = product.getPrice() - discountAmount;
        }

        if (paymentRequest.getMoney() < finalPrice) {
            throw new IllegalArgumentException(PaymentStatus.FAILED.getStatus());
        }

        int change = paymentRequest.getMoney() - finalPrice;

        Payment payment = Payment.builder()
                .productId(product.getId())
                .userId(paymentRequest.getUserId())
                .originalAmount(product.getPrice())
                .discountAmount(discountAmount)
                .finalAmount(finalPrice)
                .charge(change)
                .status(PaymentStatus.COMPLETED)
                .build();

        paymentRepository.save(payment);

        log.info("결제 완료 - userId: {}, productId: {}, finalPrice: {}, discount: {}%",
                paymentRequest.getUserId(), product.getId(), finalPrice, discountPercent);
    }
}
