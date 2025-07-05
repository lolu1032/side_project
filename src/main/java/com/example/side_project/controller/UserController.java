package com.example.side_project.controller;

import com.example.side_project.dto.Users.*;
import com.example.side_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    /**
     * 로그인
     */
    @PostMapping("/api/login")
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
    @PostMapping("/api/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SiginupRequest request) {
        SignupResponse response = service.signup(request);
        return ResponseEntity.ok(response);
        /**
         * 회원가입 로직
         */
    }
}
