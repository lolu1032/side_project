package com.example.side_project.config;

import com.example.side_project.utils.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilter() {
        FilterRegistrationBean<JwtAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthFilter(secretKey));
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }

}
