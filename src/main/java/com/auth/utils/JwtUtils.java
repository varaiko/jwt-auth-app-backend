package com.auth.utils;

import com.auth.exception.InvalidTokenException;
import com.auth.security.JwtGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@AllArgsConstructor
@Slf4j
public class JwtUtils {

    private final JwtGenerator jwtGenerator;

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Token is invalid - coming from JwtUtils");
        }
        return authHeader.substring(7);
    }

    public long getUserIdFromRequest(HttpServletRequest request) {
        String token = extractToken(request);

        if (!StringUtils.hasText(token) || !jwtGenerator.validateToken(token)) {
            log.warn("USER_WARN: Invalid or missing token");
            throw new InvalidTokenException("Invalid or missing token");
        }

        return jwtGenerator.getUserIdFromToken(token);
    }

}
