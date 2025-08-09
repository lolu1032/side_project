package com.example.side_project.repository;

import com.example.side_project.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface UserRepository extends JpaRepository<Users,Long> {
    Users findByUsername(String username);

    @Modifying
    @Query("UPDATE Users u SET u.refreshToken = :refreshToken, u.refreshTokenExpiry = :refreshTokenExpiry WHERE u.id = :id")
    void updateUserRefreshToken(
            @Param("id") Long id,
            @Param("refreshToken") String refreshToken,
            @Param("refreshTokenExpiry")Instant expire
    );
}
