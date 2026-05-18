package com.atlascv.atlascvbackend.repository;

import com.atlascv.atlascvbackend.entity.PasswordResetCode;
import com.atlascv.atlascvbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {
    Optional<PasswordResetCode> findTopByUserOrderByCreatedAtDesc(User user);
    List<PasswordResetCode> findByUserAndUsedFalse(User user);
}
