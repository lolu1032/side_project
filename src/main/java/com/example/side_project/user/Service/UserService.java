package com.example.side_project.user.Service;

import com.example.side_project.user.domain.Users;
import com.example.side_project.user.dto.Users.*;
import com.example.side_project.exception.UserErrorCode;
import com.example.side_project.user.repository.UserRepository;
import com.example.side_project.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        Users users = repository.findByUsername(request.username());

        if(users == null) {
            throw UserErrorCode.NOT_FOUNT_USERNAME.exception();
        }

        if (!encoder.matches(request.password(), users.getPassword())) {
            throw UserErrorCode.PASSWORD_MISMATCH.exception();
        }

        String accessToken = jwtUtil.generateAccessToken(request.username());

        String refreshToken;
        Instant now = Instant.now();
        if(users.getRefreshToken() == null || users.getRefreshTokenExpiry() == null || users.getRefreshTokenExpiry().isBefore(now)) {
            refreshToken = jwtUtil.generateRefreshToken(request.username());
            repository.updateUserRefreshToken(users.getId(), encoder.encode(refreshToken), Instant.now().plusSeconds(60 * 60 * 24 * 14));
        }
        Cookie cookie = cookie(users.getRefreshToken());
        response.addCookie(cookie);
        response.setHeader("Authorization",accessToken);

        return LoginResponse.builder()
                .username(users.getUsername())
                .password(users.getPassword())
                .accessToken(accessToken)
                .build();
    }

    @Transactional
    public SignupResponse signup(SiginupRequest request) {

        Users byUsername = repository.findByUsername(request.username());

        if(byUsername != null) {
            throw UserErrorCode.DUPLICATE_USERNAM.exception();
        }

        String pw = encoder.encode(request.password());

        Users users = Users.builder()
                .username(request.username())
                .password(pw)
                .isAdmin(false)
                .build();

        repository.save(users);

        return SignupResponse.builder()
                .username(users.getUsername())
                .password(users.getPassword())
                .build();

    }

    private Cookie cookie(String refreshToken) {
        Cookie refresh = new Cookie("refreshToken",refreshToken);
        refresh.setHttpOnly(true);
        refresh.setSecure(true);
        refresh.setPath("/");
        refresh.setMaxAge(60 * 60 * 24 * 14);
        return refresh;
    }
}
