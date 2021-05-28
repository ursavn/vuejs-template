package com.ursa.tools.amazon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ursa.tools.amazon.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
	PasswordResetToken findByToken(String token);
}
