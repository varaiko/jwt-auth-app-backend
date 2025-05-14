package com.auth.repository;

import com.auth.entity.PasswordReset;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {

    Optional<PasswordReset> findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM PasswordReset t WHERE t.expiryTime < :now")
    void deleteAllByExpiryDateBefore(LocalDateTime now);

}
