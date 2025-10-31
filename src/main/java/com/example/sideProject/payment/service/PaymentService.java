package com.example.sideProject.payment.service;

import com.example.sideProject.payment.domain.Payment;
import com.example.sideProject.payment.domain.PaymentStatus;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public void payment(int 쿠폰, int userId,int 돈, int 상품) {
        /**
         * 쿠폰 사용했냐 안했냐 구분
         * 결제 로직 구현
         */
        상품 상품 = 상품repository.findById(상품);
        if(돈 < 상품.가격) {
            throw new IllegalAccessException("돈이 안맞음 결제 실패 진행");
        }

        if(쿠폰 <= 0) {
            /**
             * 쿠폰 안쓰는거임
             */
            int 거스름돈 = 돈 - 상품.가격;
            Payment payment = Payment.builder()
                    .couponIssuesId(0L)
                    .discountAmount(0L)
                    .finalAmount(거스름돈)
                    .status(PaymentStatus.COMPLETED)
                    .productId(상품)
                    .originalAmount(상품.가격)
                    .userId(userId)
                    .build();
            paymentRepository.save(payment);
        }else {
            /**
             * 쿠폰 써서 결제임
             */
            int 할인금액 = 상품.가격 * 쿠폰.할인율 / 100;
            int 거스름돈 = 돈 - 할인금액;
            Payment payment = Payment.builder()
                    .couponIssuesId(쿠폰.id)
                    .discountAmount(쿠폰.할인율)
                    .finalAmount(거스름돈)
                    .status(PaymentStatus.COMPLETED)
                    .productId(상품)
                    .originalAmount(상품.가격)
                    .userId(userId)
                    .build();
            paymentRepository.save(payment);
        }
    }
}
