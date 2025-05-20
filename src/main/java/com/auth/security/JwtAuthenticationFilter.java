package com.auth.security;

import com.auth.dto.request.ExceptionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.auth.exception.InvalidTokenException;
import com.auth.service.implementation.UserServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtGenerator tokenGenerator;
    private final UserServiceImpl customUserDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getJwtFromRequest(request);

            if (StringUtils.hasText(token)) {
                log.debug("Found authorization header, attemtping to validate JWT for URI {}", request.getRequestURI());
            } else {
                log.debug("No JWT present in request for URI {}", request.getRequestURI());
            }

            if (StringUtils.hasText(token) && tokenGenerator.validateToken(token)) {
                Claims claims = tokenGenerator.getClaims(token);
                String tokenType = claims.get("token_type", String.class);

                if (!"access".equals(tokenType)) {
                    log.warn("JWT with wrong type {} used to access {}", tokenType, request.getRequestURI());
                    throw new InvalidTokenException("Invalid token type for accessing resources");
                }

                UserDetails userDetails = customUserDetailsService.loadUserDataWithAuthorities(request);

                log.debug("JWT valid. Authenticating user {} with auhorities {}", userDetails.getUsername(), userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        } catch (InvalidTokenException ex) {
            log.warn("Unauthorized request to {}: {}", request.getRequestURI(), ex.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            ExceptionDto exceptionDto = ExceptionDto.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .details(request.getRequestURI())
                .build();
            objectMapper.writeValue(response.getWriter(), exceptionDto);
            response.getWriter().flush();
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        if (request.getDispatcherType() == DispatcherType.ERROR) {
            return true;
        }

        String path = request.getServletPath();

        if (path.equals("/api/auth/login")
                || path.equals("/api/auth/register")
                || path.equals("/api/auth/token/verify")
                || path.equals("/api/auth/token/refresh")
                || path.equals("/api/auth/password/verify")) {
            return true;
        }

        return false;
    }

}
