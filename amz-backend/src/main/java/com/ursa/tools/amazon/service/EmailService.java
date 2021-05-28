package com.ursa.tools.amazon.service;

public interface EmailService {
	public void sendSimpleMessage(String to, String subject, String text);  
}
