package com.example.sideProject.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public final class Users {

    @Builder
    public record LoginRequest(
            @NotBlank(message = "아이디 입력은 필수입니다.")
            @Size(min = 4,max = 20, message = "아이디는 4자 이상 20자 이하로 입력해야 합니다.")
            String username,
            @NotBlank(message = "비밀번호 입력은 필수입니다.")
            @Size(min = 8, max = 30, message = "비밀번호는 8자 이상 30자 이하로 입력해야 합니다.")
            String password
    ) {
    }

    @Builder
    public record LoginResponse(
            String username,
            String password,
            String accessToken
    ){

    }

    @Builder
    public record SiginupRequest(
            @NotBlank(message = "아이디 입력은 필수입니다.")
            @Size(min = 4,max = 20, message = "아이디는 4자 이상 20자 이하로 입력해야 합니다.")
            String username,
            @NotBlank(message = "비밀번호 입력은 필수입니다.")
            @Size(min = 8, max = 30, message = "비밀번호는 8자 이상 30자 이하로 입력해야 합니다.")
            @Pattern(regexp = ".*[0-9].*", message = "비밀번호는 최소 하나 이상의 숫자를 포함해야 합니다.")
            @Pattern(regexp = ".*[a-zA-Z].*", message = "비밀번호는 최소 하나 이상의 영문자를 포함해야 합니다.")
            @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*", message = "비밀번호는 최소 하나 이상의 특수문자를 포함해야 합니다.")
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
