package com.example.side_project.controller;

import com.example.side_project.dto.Users.*;
import com.example.side_project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 인증 및 회원 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService service;

    /**
     * 로그인
     */
    @Operation(summary = "로그인", description = "사용자가 로그인을 합니다.")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "400", description = "아이디를 입력하세요."),
            @ApiResponse(responseCode = "400", description = "비밀번호를 입력하세요."),
            @ApiResponse(responseCode = "404", description = "아이디가 존재하지않습니다."),
            @ApiResponse(responseCode = "401", description = "비밀번호가 일치하지 않습니다."),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = service.login(request);

        return ResponseEntity.ok(response);
        /**
         * 로그인 로직
         */
    }

    /**
     * 회원가입
     */
    @Operation(summary = "회원가입", description = "새 사용자를 등록합니다.")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "400", description = "아이디를 입력하세요."),
            @ApiResponse(responseCode = "400", description = "아아디는 4자 이상 20자 이하로 입력해야합니다."),
            @ApiResponse(responseCode = "400", description = "비밀번호를 입력하세요."),
            @ApiResponse(responseCode = "400", description = "비밀번호는 8자 이상 30자 이하로 입력해야합니다."),
            @ApiResponse(responseCode = "400", description = "비밀번호는 최소 하나 이상의 숫자를 포함해야 합니다."),
            @ApiResponse(responseCode = "400", description = "비밀번호는 최소 하나 이상의 영문자를 포함해야 합니다."),
            @ApiResponse(responseCode = "404", description = "비밀번호는 최소 하나 이상의 특수문자를 포함해야 합니다."),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 아이디입니다."),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SiginupRequest request) {
        SignupResponse response = service.signup(request);
        return ResponseEntity.ok(response);
        /**
         * 회원가입 로직
         */
    }
}
