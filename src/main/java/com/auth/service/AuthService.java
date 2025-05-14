package com.auth.service;

import com.auth.dto.request.LoginRequestDto;
import com.auth.dto.request.RegisterRequestDto;
import com.auth.dto.request.ResetPasswordRequestDto;
import com.auth.dto.request.VerifyResetTokenRequestDto;
import com.auth.dto.response.AuthResponseDto;
import com.auth.dto.response.SimpleResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    AuthResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request);

    SimpleResponse register(RegisterRequestDto registerRequestDto, HttpServletRequest request);

    SimpleResponse verifyPasswordResetToken(VerifyResetTokenRequestDto verifyResetTokenRequestDto, HttpServletRequest request);

    SimpleResponse generateForgotPasswordToken(String email, HttpServletRequest request);

    SimpleResponse resetPassword(ResetPasswordRequestDto resetPasswordRequestDto, HttpServletRequest request);
}
