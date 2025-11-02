package com.example.sideProject.payment.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class PaymentResponse {
    private String message;
    private HttpStatus status;
}
