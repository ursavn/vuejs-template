package com.ursa.tools.amazon.payload;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
	private String email;
	private String token;
	private String password;
}
