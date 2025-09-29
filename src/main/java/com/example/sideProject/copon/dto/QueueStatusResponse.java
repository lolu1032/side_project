package com.example.sideProject.copon.dto;
import com.example.sideProject.copon.constant.QueueType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueueStatusResponse {
    private QueueType status;
    private long position;
    private long totalWaiting;
    private String message;
}