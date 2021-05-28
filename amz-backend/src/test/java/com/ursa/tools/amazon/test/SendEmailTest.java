package com.ursa.tools.amazon.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ursa.tools.amazon.service.EmailService;

@SpringBootTest
public class SendEmailTest {
	
	@Autowired
	EmailService emailService;
	
	//@Test
	public void sendTokenConfirmPassword() {
		//emailService.sendSimpleMessage("tuyen.ta@ursa.vn", "Confirm Password", "Token: ABCC");
	}

}
