package com.example.side_project.dto;

import lombok.Builder;

public final class Users {

    @Builder
    public record loginRequest(
            String username,
            String password
    ) {
    }

    @Builder
    public record siginupRequest(
            String username,
            String password
    ){

    }
}
