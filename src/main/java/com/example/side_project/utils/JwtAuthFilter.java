package com.example.side_project.utils;

import com.example.side_project.exception.CouponErrorCode;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JwtAuthFilter implements Filter {

    private final String secretKey;

    public JwtAuthFilter(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        if(path.equals("/api/login") || path.equals("/api/signup") || path.startsWith("/api/interview/")) {
            filterChain.doFilter(request,response);
            return;
        }

        String authHeader = request.getHeader("authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(CouponErrorCode.NOT_FOUND_TOKEN.status().value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"" + CouponErrorCode.NOT_FOUND_TOKEN.message() + "\"}");
            return;
        }

        String token = authHeader.replace("Bearer ", "").trim();

        try {
            Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(CouponErrorCode.EXPIRED_TOKEN.status().value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"" + CouponErrorCode.EXPIRED_TOKEN.message() + "\"}");
        }
    }
}
