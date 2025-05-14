package com.auth.service.implementation;

import com.auth.dto.request.LoginRequestDto;
import com.auth.dto.request.RegisterRequestDto;
import com.auth.dto.request.ResetPasswordRequestDto;
import com.auth.dto.request.VerifyResetTokenRequestDto;
import com.auth.dto.response.AuthResponseDto;
import com.auth.entity.PasswordReset;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.exception.ApiException;
import com.auth.exception.ResourceNotFoundException;
import com.auth.exception.UsernameAlreadyExistsException;
import com.auth.repository.PasswordResetRepository;
import com.auth.repository.RoleRepository;
import com.auth.repository.UserRepository;
import com.auth.security.JwtGenerator;
import com.auth.service.AuthService;
import com.auth.dto.response.SimpleResponse;
import com.auth.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtGenerator jwtGenerator;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailServiceImpl emailService;

    // Login method
    public AuthResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtGenerator.generateToken(authentication);
            String refreshToken = jwtGenerator.generateRefreshToken(loginRequestDto.getUsername());

            User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> {
                    log.warn("LOGIN_FAIL: No such user with e-mail {} from IP {}", loginRequestDto.getUsername(), request.getRemoteAddr());
                    return new UsernameNotFoundException("No such user");
                });

            user.setLastLoginDate(LocalDateTime.now());

            log.info("LOGIN_SUCCESS: User login successful: ID {} username {} from IP {}", user.getId(), user.getUsername(), request.getRemoteAddr());

            userRepository.save(user);

            return new AuthResponseDto(token, refreshToken);
        } catch (BadCredentialsException ex) {
            log.warn("LOGIN_FAIL: Bad credentials for username {} from IP {}", loginRequestDto.getUsername(), request.getRemoteAddr());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    // Register method
    @Transactional
    public SimpleResponse register(RegisterRequestDto registerRequestDto, HttpServletRequest request) {

        if(userRepository.existsByUsername(registerRequestDto.getUsername())) {
            log.warn("REGISTER_FAIL: User already exists with username {} from IP {}", registerRequestDto.getUsername(), request.getRemoteAddr());
            throw new UsernameAlreadyExistsException();
        }

        User user = new User();
        user.setUsername(registerRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));

        Role role = roleRepository.findByName("USER");
        if (role == null) {
            log.error("REGISTER_ERROR: Required role USER not found during registration for username {}", registerRequestDto.getUsername());
            throw new ResourceNotFoundException("Role", "name", "USER");
        }

        user.setRoles(role);
        userRepository.save(user);

        log.info("REGISTER_SUCCESS: User registered successfully: id {} username {} from IP {}", user.getId(), registerRequestDto.getUsername(), request.getRemoteAddr());

        return new SimpleResponse(true, "User has been successfully registered");
    }

    public SimpleResponse verifyPasswordResetToken(VerifyResetTokenRequestDto verifyResetTokenRequestDto, HttpServletRequest request) {

        PasswordReset passwordReset = passwordResetRepository.findByToken(verifyResetTokenRequestDto.getToken()).orElseThrow(() -> new InvalidTokenException("Token has expired or is not valid"));

        if (passwordReset.getExpiryTime().isBefore(LocalDateTime.now())) {
            log.warn("PASSWORD_RESET_VERIFY_EXPIRED: Token has expired for username {} from IP {}", passwordReset.getEmail(), request.getRemoteAddr());
            throw new InvalidTokenException("Token has expired");
        }

        log.info("PASSWORD_RESET_VERIFY_SUCCESS:Token is valid for password reset for username {} from IP {}", passwordReset.getEmail(), request.getRemoteAddr());

        return new SimpleResponse(true, "Token is valid");
    }

    public SimpleResponse generateForgotPasswordToken(String email, HttpServletRequest request) {

        Optional<User> user = userRepository.findByUsername(email);

        String token = UUID.randomUUID().toString();

        if (user.isPresent()) {
            PasswordReset passwordReset = new PasswordReset();
            passwordReset.setEmail(email);
            passwordReset.setToken(token);
            passwordReset.setExpiryTime(LocalDateTime.now().plusMinutes(30));

            passwordResetRepository.save(passwordReset);
            String resetLink = "http://localhost:3000/reset-password?token=" + token;

            emailService.sendEmail(email, resetLink);

            log.info("PASSWORD_RESET_REQUEST_SUCCESS: Password reset link has been sent to e-mail {} from IP {}", email, request.getRemoteAddr());
        } else {
            log.info("PASSWORD_RESET_REQUEST_UNKNOWN: No such user with e-mail {} from IP {}", email, request.getRemoteAddr());
        }

        return new SimpleResponse(true, "Password reset link has been sent to your email if the user exists");
    }

    // Reset password method (user resets the password via link sent on email)
    public SimpleResponse resetPassword(ResetPasswordRequestDto resetPasswordRequestDto, HttpServletRequest request) {

        PasswordReset passwordReset = passwordResetRepository.findByToken(resetPasswordRequestDto.getToken())
            .orElseThrow(() -> {
                log.error("PASSWORD_RESET_FAIL_NO_TOKEN: No such token or token has expired from IP {}", request.getRemoteAddr());
                return new InvalidTokenException("Token has expired or is not valid");
            });

        if (passwordReset.getExpiryTime().isBefore(LocalDateTime.now())) {
            log.warn("PASSWORD_RESET_FAIL_EXPIRED:Token has expired for username {}", passwordReset.getEmail());
            throw new InvalidTokenException("Password reset link has expired");
        }

        User user = userRepository.findByUsername(passwordReset.getEmail())
            .orElseThrow(() -> {
                log.error("PASSWORD_RESET_FAIL_NO_USER: No such user with email {} from IP {}", passwordReset.getEmail(), request.getRemoteAddr());
                return new UsernameNotFoundException("No user with email " + passwordReset.getEmail());
            });

        user.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword()));
        userRepository.save(user);

        passwordResetRepository.delete(passwordReset);

        log.info("PASSWORD_RESET_SUCCESS: Password has been reset for username {} from IP {}", passwordReset.getEmail(), request.getRemoteAddr());

        return new SimpleResponse(true, "Password has been reset");
    }
}
