package com.auth.service.implementation;

import com.auth.repository.PasswordResetRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class PasswordResetTokenCleanupService {

    private final PasswordResetRepository passwordResetRepository;

    @Scheduled(fixedRateString = "${token.cleanup.rate:3600000}")
    public void deleteExpiredTokens() {
        LocalDateTime date = LocalDateTime.now();
        passwordResetRepository.deleteAllByExpiryDateBefore(date);
        log.info("RESET_TOKEN_CLEANUP: All expired tokens have been cleared from database");
    }
}
