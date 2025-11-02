package com.example.sideProject.payment.service;

import com.example.sideProject.copon.domain.Coupon;
import com.example.sideProject.copon.domain.Promotion;
import com.example.sideProject.copon.repository.CouponRepository;
import com.example.sideProject.copon.repository.PromotionRepository;
import com.example.sideProject.exception.CouponErrorCode;
import com.example.sideProject.exception.PromotionErrorCode;
import com.example.sideProject.payment.domain.Payment;
import com.example.sideProject.payment.domain.PaymentStatus;
import com.example.sideProject.payment.domain.Product;
import com.example.sideProject.payment.dto.PaymentRequest;
import com.example.sideProject.payment.dto.PaymentResponse;
import com.example.sideProject.payment.exception.ProductErrorCode;
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

    public PaymentResponse payment(PaymentRequest paymentRequest) {
        /**
         * 쿠폰 사용했냐 안했냐 구분
         * 결제 로직 구현
         */
        Product product = productRepository.findById(paymentRequest.getProductId())
                .orElseThrow(ProductErrorCode.NOT_FOUND::exception);

        Coupon coupon = null;
        Promotion promotion = null;
        if (paymentRequest.getCouponId() != null && couponRepository.existsById(paymentRequest.getCouponId())) {
            coupon = couponRepository.findById(paymentRequest.getCouponId())
                    .orElseThrow(CouponErrorCode.NOT_FOUND_COUPON::exception);

            if (coupon.getPromotionId() != null) {
                promotion = promotionRepository.findById(coupon.getPromotionId())
                        .orElseThrow(PromotionErrorCode.NOT_FOUND::exception);
            }
        }
        int discountPercent = 0;
        int discountAmount = 0;
        int finalPrice = product.getPrice();
        /**
         * coupon이 null아니면서 사용안했을때
         * 계싼
         */
        if (coupon != null && !coupon.isUsed()) {
            discountPercent = promotion.getDiscount();
            discountAmount = product.getPrice() * discountPercent / 100;
            finalPrice = product.getPrice() - discountAmount;
        }

        if (paymentRequest.getMoney() < finalPrice) {
            throw ProductErrorCode.INSUFFICIENT_FUNDS.exception();
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

        return PaymentResponse.builder()
                .message(PaymentStatus.COMPLETED.getMessage())
                .status(PaymentStatus.COMPLETED.getStatus())
                .build();
    }
}
