package com.auth.service.implementation;

import com.auth.utils.JwtUtils;
import com.auth.dto.response.AuthResponseDto;
import com.auth.entity.User;
import com.auth.exception.InvalidTokenException;
import com.auth.repository.UserRepository;
import com.auth.security.JwtGenerator;
import com.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final JwtGenerator jwtGenerator;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public AuthResponseDto refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("AUTH_WARN: Authorization header missing or invalid");
            throw new InvalidTokenException("Authorization header missing or invalid");
        }

        String refreshToken = authHeader.substring(7);

        if (!jwtGenerator.validateToken(refreshToken)) {
            log.warn("AUTH_WARN: Invalid refresh token");
            throw new InvalidTokenException("Invalid refresh token");
        }

        Claims claims = jwtGenerator.getClaims(refreshToken);
        String tokenType = claims.get("token_type", String.class);
        if (!"refresh".equals(tokenType)) {
            log.error("AUTH_WARN: Invalid refresh token, received {}", tokenType);
            throw new InvalidTokenException("Invalid refresh token");
        }

        long userId = Long.valueOf(claims.getSubject());

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("AUTH_ERROR: No user with ID {}", userId);
            return new InvalidTokenException("No user with ID " + userId);
        });

        String accessToken = jwtGenerator.generateTokenOnRefresh(user);
        String newRefreshToken = jwtGenerator.generateRefreshToken(user.getUsername());

        log.info("AUTH_SUCCESS: User authenticated with username {} from IP {}", user.getId(), request.getRemoteAddr());

        return AuthResponseDto.builder().accessToken(accessToken).refreshToken(newRefreshToken).build();
    }

    public void verifyToken(HttpServletRequest request) {
        String token = jwtUtils.extractToken(request);

        if (token == null || !jwtGenerator.validateToken(token)) {
            log.error("AUTH_ERROR: Token is invalid or expired");
            throw new InvalidTokenException("Token is invalid or expired");
        }

    }

}
