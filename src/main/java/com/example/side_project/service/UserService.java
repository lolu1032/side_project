package com.example.side_project.service;

import com.example.side_project.domain.Users;
import com.example.side_project.dto.Users.*;
import com.example.side_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder encoder;
    private final UserRepository repository;

    @Transactional
    public void login(loginRequest request) {
        Users byUsername = repository.findByUsername(request.username());

        if(byUsername == null) {
            throw new IllegalArgumentException("아이디가 존재하지않습니다.");
        }

        if(!encoder.matches(byUsername.getPassword(), request.password())) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }

    }

    @Transactional
    public void signup(siginupRequest request) {
        validateUsername(request.username());

        Users byUsername = repository.findByUsername(request.username());

        if(byUsername != null) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        validatePassword(request.password());

        String pw = encoder.encode(request.password());

        Users users =Users.builder()
                .username(request.username())
                .password(pw)
                .is_admin(false)
                .build();

        repository.save(users);

    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("아이디를 입력하세요.");
        }
        if (username.length() < 4 || username.length() > 20) {
            throw new IllegalArgumentException("아이디는 4자 이상 20자 이하로 입력해야 합니다.");
        }
        if (!username.matches("^[a-zA-Z0-9]*$")) {
            throw new IllegalArgumentException("아이디는 영문자와 숫자만 사용할 수 있습니다.");
        }
    }
    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력하세요.");
        }
        if (password.length() < 8 || password.length() > 30) {
            throw new IllegalArgumentException("비밀번호는 8자 이상 30자 이하로 입력해야 합니다.");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("비밀번호는 최소 하나 이상의 숫자를 포함해야 합니다.");
        }
        if (!password.matches(".*[a-zA-Z].*")) {
            throw new IllegalArgumentException("비밀번호는 최소 하나 이상의 영문자를 포함해야 합니다.");
        }
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new IllegalArgumentException("비밀번호는 최소 하나 이상의 특수문자를 포함해야 합니다.");
        }
    }
}
