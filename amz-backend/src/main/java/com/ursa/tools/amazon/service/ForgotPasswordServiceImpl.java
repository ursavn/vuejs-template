package com.ursa.tools.amazon.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ursa.tools.amazon.model.PasswordResetToken;
import com.ursa.tools.amazon.repository.PasswordResetTokenRepository;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService{

	@Autowired
	PasswordResetTokenRepository passwordTokenRepository;
	
	@Override
	@Transactional
	public PasswordResetToken save(PasswordResetToken token) {
		return passwordTokenRepository.save(token);
	}

	@Override
	public PasswordResetToken findByToken(String token) {
		return passwordTokenRepository.findByToken(token);
	}

}
