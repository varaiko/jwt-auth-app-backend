package com.auth.controller;

import com.auth.dto.request.*;
import com.auth.dto.response.AuthResponseDto;
import com.auth.service.AuthService;
import com.auth.service.JwtService;
import com.auth.dto.response.SimpleResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
        AuthResponseDto response = authService.login(loginRequestDto, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<SimpleResponse> register(@RequestBody RegisterRequestDto registerRequestDto, HttpServletRequest request) {
        SimpleResponse response = authService.register(registerRequestDto, request);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<SimpleResponse> forgotPassword(@RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto, HttpServletRequest request) {
        SimpleResponse authResponse = authService.generateForgotPasswordToken(forgotPasswordRequestDto.getEmail(), request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<SimpleResponse> resetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto, HttpServletRequest request) {
        SimpleResponse response = authService.resetPassword(resetPasswordRequestDto, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/verify")
    public ResponseEntity<SimpleResponse> verifyResetToken(@RequestBody VerifyResetTokenRequestDto verifyResetTokenRequestDto, HttpServletRequest request) {
        SimpleResponse response = authService.verifyPasswordResetToken(verifyResetTokenRequestDto, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken (HttpServletRequest request) {
        AuthResponseDto authResponse = jwtService.refreshToken(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/token/verify")
    public ResponseEntity<?> verifyToken (HttpServletRequest request) {
        jwtService.verifyToken(request);
        return ResponseEntity.ok().build();
    }

}
