package com.example.side_project.domain;

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
    private boolean is_admin;
    private Instant created_at;
    private Instant updated_at;

    @PrePersist
    public void prePersist() {
        this.created_at = Instant.now();
    }

}
