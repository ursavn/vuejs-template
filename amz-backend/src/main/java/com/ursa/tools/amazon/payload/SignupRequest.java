package com.ursa.tools.amazon.payload;

import lombok.Data;

@Data
public class SignupRequest {

	private String email;
	private String password;
	private String fullName;
	
	public SignupRequest(String email, String password, String fullName) {
		this.email = email;
		this.password = password;
		this.fullName = fullName;
	}	
	
}
