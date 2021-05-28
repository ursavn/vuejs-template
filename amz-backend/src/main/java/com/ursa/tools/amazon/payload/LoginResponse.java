package com.ursa.tools.amazon.payload;

import com.ursa.tools.amazon.constant.enums.UserRole;

import lombok.Data;

@Data
public class LoginResponse {
	private Integer id;	 
	private String email;
	private String fullName;
	private String token;
	private UserRole role;
	
	public LoginResponse(String token, Integer id, String email, String fullName, UserRole role) {
		this.token = token;
		this.id = id;
		this.email = email;
		this.fullName = fullName;		
		this.role = role;
	}
		
}
