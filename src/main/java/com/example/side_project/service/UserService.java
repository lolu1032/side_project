package com.example.side_project.service;

import com.example.side_project.domain.Users;
import com.example.side_project.dto.Users.*;
import com.example.side_project.exception.UserErrorCode;
import com.example.side_project.repository.UserRepository;
import com.example.side_project.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UserRepository repository;
    private final JwtUtil jwtUtil;

    /**
     * JWT 인증 추가 하기
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Users byUsername = repository.findByUsername(request.username());

        if(byUsername == null) {
            throw UserErrorCode.NOT_FOUNT_USERNAME.exception();
        }

        if (!encoder.matches(request.password(), byUsername.getPassword())) {
            throw UserErrorCode.PASSWORD_MISMATCH.exception();
        }

        String accessToken = jwtUtil.generateAccessToken(request.username());
        String refreshToken = jwtUtil.generateRefreshToken(request.username());

        return LoginResponse.builder()
                .username(byUsername.getUsername())
                .password(byUsername.getPassword())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * JWT 인증 추가 하기
     */
    @Transactional
    public SignupResponse signup(SiginupRequest request) {
        validateUsername(request.username());
        validatePassword(request.password());

        Users byUsername = repository.findByUsername(request.username());

        if(byUsername != null) {
            throw UserErrorCode.DUPLICATE_USERNAM.exception();
        }

        String pw = encoder.encode(request.password());

        Users users = Users.builder()
                .username(request.username())
                .password(pw)
                .is_admin(false)
                .build();

        repository.save(users);

        return SignupResponse.builder()
                .username(users.getUsername())
                .password(users.getPassword())
                .build();

    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw UserErrorCode.EMPTY_USERNAME.exception();
        }
        if (username.length() < 4 || username.length() > 20) {
            throw UserErrorCode.INVALID_USERNAME.exception();
        }
    }
    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw UserErrorCode.EMPTY_PASSWORD.exception();
        }
        if (password.length() < 8 || password.length() > 30) {
            throw UserErrorCode.INVALID_PASSWORD_LENGTH.exception();
        }
        if (!password.matches(".*[0-9].*")) {
            throw UserErrorCode.PASSWORD_NO_NUMBER.exception();
        }
        if (!password.matches(".*[a-zA-Z].*")) {
            throw UserErrorCode.PASSWORD_NO_LETTER.exception();
        }
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw UserErrorCode.PASSWORD_NO_SPECIAL.exception();
        }
    }
}
