package com.auth.service;

import com.auth.dto.response.AuthResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface JwtService {

    AuthResponseDto refreshToken(HttpServletRequest request);

    void verifyToken(HttpServletRequest request);

}
