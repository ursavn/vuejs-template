package com.ursa.tools.amazon.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ursa.tools.amazon.payload.LineStatusResponse;
import com.ursa.tools.amazon.payload.LineTokenRequest;
import com.ursa.tools.amazon.service.LineNotifyService;

@SpringBootTest
public class LineTokenTest {
	
	@Autowired
	LineNotifyService lineNotifyService;

	//@Test
	public void getToken() {
		LineTokenRequest request = new LineTokenRequest();
		request.setCode("mcHciQGj7MXO5LYjKvTnqZ");
		request.setClientId("oJdZG6hNNiDWlsB29Wrr6K");
		request.setClientSecret("NKutBeMupgL5x27vK4QetAJKrKTWAawR0WZ5RIOvk6O");
		request.setGrantType("authorization_code");
		request.setRedirectUri("https://testamazon.ursa.vn/callback");
		
		lineNotifyService.getAccessToken(request);
	}
	
	//@Test
	public void getStatus() {
		String token = "it2r7hJuxW0bg4Bkec3f9gPmwOeHxFIpxYnC6gy1iqs";		
		LineStatusResponse response = lineNotifyService.getStatus(token);
		System.out.println(response); 
	}
}
