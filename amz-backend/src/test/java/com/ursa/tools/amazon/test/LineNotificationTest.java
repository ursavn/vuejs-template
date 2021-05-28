package com.ursa.tools.amazon.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ursa.tools.amazon.service.LineNotifyService;

@SpringBootTest
public class LineNotificationTest {

	@Autowired
	LineNotifyService lineNotifyService;
	
	//@Test
	public void testNotify() {
		String lineMessage = "Hello team";
		try {
			lineNotifyService.sendNotify(lineMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
