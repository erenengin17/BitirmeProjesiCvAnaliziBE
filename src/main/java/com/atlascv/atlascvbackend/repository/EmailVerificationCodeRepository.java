package com.atlascv.atlascvbackend.repository;

import com.atlascv.atlascvbackend.entity.EmailVerificationCode;
import com.atlascv.atlascvbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {

    List<EmailVerificationCode> findByUserAndUsedFalse(User user);

    Optional<EmailVerificationCode> findTopByUserOrderByCreatedAtDesc(User user);
}