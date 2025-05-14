package com.auth.security;

import com.auth.entity.User;
import com.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
@AllArgsConstructor
public class JwtGenerator {

    private final UserRepository userRepository;

    private SecretKey getSignInKey() {
        byte[] bytes = Base64.getDecoder()
                .decode(SecurityConstants.JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(bytes, "HmacSHA512"); }

    public String generateToken(Authentication authentication) {

        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("No user with email " + authentication.getName()));

        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

        String token = Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(new Date())
                .expiration(expiryDate)
                .claim("token_type", "access")
                .signWith(getSignInKey(), Jwts.SIG.HS512)
                .compact();
        return token;
    }

    public String generateTokenOnRefresh(User user) {
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(currentDate)
                .expiration(expiryDate)
                .claim("token_type", "access") // add the token type!
                .signWith(getSignInKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String generateRefreshToken(String username) {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No user with email " + username));

        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + SecurityConstants.REFRESH_TOKEN_EXPIRATION);

        String token = Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(new Date())
                .expiration(expiryDate)
                .claim("token_type", "refresh")
                .signWith(getSignInKey(), Jwts.SIG.HS512)
                .compact();
        return token;
    }

    public long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();

        return Long.valueOf(claims.getSubject());
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException ex) {
            log.info("JWT expired for user ID {}", ex.getClaims().getSubject());
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Invalid JWT token", ex.getMessage());
        }
        return false;
    }

}
