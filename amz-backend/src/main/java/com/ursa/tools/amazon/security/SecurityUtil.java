package com.ursa.tools.amazon.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ursa.tools.amazon.model.User;

public class SecurityUtil {
	public static User getAuthenticationUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return ((UserDetailsImpl) authentication.getPrincipal()).getUser();
	}
	
	public static Integer getAuthenticationUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return ((UserDetailsImpl) authentication.getPrincipal()).getId();
	}
	
	public static String getAuthenticationUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
	}
}
