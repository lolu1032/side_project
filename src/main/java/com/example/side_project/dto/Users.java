package com.example.side_project.dto;

import lombok.Builder;

public final class Users {

    @Builder
    public record LoginRequest(
            String username,
            String password
    ) {
    }

    @Builder
    public record LoginResponse(
            String username,
            String password
    ){

    }

    @Builder
    public record SiginupRequest(
            String username,
            String password
    ){
    }

    @Builder
    public record SignupResponse(
            String username,
            String password
    ){
    }
}
