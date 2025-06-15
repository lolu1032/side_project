package com.example.side_project.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    /**
     * 로그인
     */
    @PostMapping("/api/login")
    public void login() {
        /**
         * 로그인 로직
         */
    }

    /**
     * 회원가입
     */
    @PostMapping("/api/signup")
    public void signup() {
        /**
         * 회원가입 로직
         */
    }
}
