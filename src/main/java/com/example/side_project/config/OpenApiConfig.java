package com.example.side_project.config;

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
                    new Tag().name("Coupon_V1").description("쿠폰 V1 API(비관적 락)"),
                    new Tag().name("Coupon_V2").description("쿠폰 V2 API (Redis)"),
                    new Tag().name("Coupon_V3").description("쿠폰 V3 API(synchronized)")
            ));
        };
    }

}