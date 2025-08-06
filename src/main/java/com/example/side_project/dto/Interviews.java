package com.example.side_project.dto;

import lombok.Builder;


public final class Interviews {
    @Builder
    public record Request(
            String applicantName,
            String company,
            String location,
            String startDatetime,
            String endDatetime,
            String note,
            String timeRange
    ){}
    @Builder
    public record FaileRequest(
            String company,
            String note
    ){}

    @Builder
    public record Response(

    ){}
}
