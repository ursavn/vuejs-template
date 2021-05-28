package com.ursa.tools.amazon.service;

import com.ursa.tools.amazon.model.PasswordResetToken;

public interface ForgotPasswordService {
    PasswordResetToken save(PasswordResetToken token);
    PasswordResetToken findByToken(String token);    
}
