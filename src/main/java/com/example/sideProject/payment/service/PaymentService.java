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
                .orElseThrow();
        Coupon coupon = couponRepository.findById(paymentRequest.getCouponId())
                .orElseThrow();
        Promotion promotion = promotionRepository.findById(coupon.getPromotionId())
                .orElseThrow();
        if(paymentRequest.getMoney() < product.getPrice()) {
            throw new IllegalArgumentException("돈이 안맞음 결제 실패 진행");
        }

        if(!couponRepository.existsById(paymentRequest.getCouponId())) {
            log.info("노쿠폰");
            /**
             * 쿠폰 안쓰는거임
             */
            int change = paymentRequest.getMoney() - product.getPrice();
            Payment payment = Payment.builder()
                    .discountAmount(0)
                    .finalAmount(change)
                    .status(PaymentStatus.COMPLETED)
                    .productId(paymentRequest.getProductId())
                    .originalAmount(product.getPrice())
                    .userId(paymentRequest.getUserId())
                    .build();
            paymentRepository.save(payment);
        }else {
            log.info("쿠폰");
            /**
             * 쿠폰 써서 결제임
             */
            int saleAccount = product.getPrice() * promotion.getDiscount() / 100;
            int finalPrice = product.getPrice() - saleAccount;
            int change = paymentRequest.getMoney() - finalPrice;
            Payment payment = Payment.builder()
                    .discountAmount(promotion.getDiscount())
                    .finalAmount(finalPrice)
                    .status(PaymentStatus.COMPLETED)
                    .productId(paymentRequest.getProductId())
                    .originalAmount(product.getPrice())
                    .userId(paymentRequest.getUserId())
                    .charge(change)
                    .build();
            paymentRepository.save(payment);
        }
    }
}
