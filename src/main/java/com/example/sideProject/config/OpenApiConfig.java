package com.example.sideProject.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("https://api.dalcol.shop").description("운영 서버"),
                        new Server().url("http://localhost:8080").description("로컬서버")
                ))
                .info(new Info()
                        .title("API Documentation")
                        .version("1.0"))
                .components(new Components()
                    .addSecuritySchemes("BearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
    }

    @Bean
    public OpenApiCustomizer sortTagsCustomizer() {
        return openApi -> {
            openApi.setTags(Arrays.asList(
                    new Tag().name("User").description("사용자 관련 API"),
                    new Tag().name("Coupon_Row_lock").description("쿠폰 API(row lock)"),
                    new Tag().name("Coupon").description("""
        쿠폰 통합 API promotion : 1~10, userId : 1
        type
        - redis: Redis 기반 재고 처리
        - synchronized: 서버 메모리 기반 동기화 처리
        - atomic: AtomicInteger 기반 경쟁 제어 처리
    """),
                    new Tag().name("Coupon Queue").description("쿠폰 대기열 API (Redis)")
            ));
        };
    }

}