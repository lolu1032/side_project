package com.example.sideProject.interview.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FailNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;

    /**
     * JPA String은  기본값으로 varchar(255)로 받아드림
     * 그래서 지금까지 varchar(3000) / text로 해도 에러가 남
     * @column()을 통해 columnDefinition으로 타입 명시해줘야 인식
     * JPA는 바보다
     */
    @Column(columnDefinition = "text", name = "memo")
    private String memo;
}
