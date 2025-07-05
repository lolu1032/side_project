package com.example.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;

@Builder
public record ApiError(
        String type,
        @JsonInclude(Include.NON_NULL)
        String title,
        @JsonInclude(Include.NON_NULL)
        Integer status,
        @JsonInclude(Include.NON_NULL)
        String detail,
        @JsonInclude(Include.NON_NULL)
        String instance
) {
    public ApiError {
        if (type == null || type.isBlank()) {
            type = "about:blank";
        }
    }
}