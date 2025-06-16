package com.example.side_project.controller;

import com.example.side_project.dto.Users.*;
import com.example.side_project.service.UserService;
import lombok.RequiredArgsConstructor;
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
    public void login(@RequestBody loginRequest request) {
        service.login(request);
        /**
         * 로그인 로직
         */
    }

    /**
     * 회원가입
     */
    @PostMapping("/api/signup")
    public void signup(@RequestBody siginupRequest request) {
        service.signup(request);
        /**
         * 회원가입 로직
         */
    }
}
