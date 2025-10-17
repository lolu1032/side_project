package com.example.sideProject.copon.dto;
import com.example.sideProject.exception.QueueErrorCode;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueueStatusResponse {
    private QueueErrorCode status;
    private long position;
    private long totalWaiting;
    private String message;
}
