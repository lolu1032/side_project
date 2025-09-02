package com.example.side_project.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private boolean isAdmin;
    private Instant createdAt;
    private Instant updatedAt;
    private String refreshToken;
    private Instant refreshTokenExpiry;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

}
