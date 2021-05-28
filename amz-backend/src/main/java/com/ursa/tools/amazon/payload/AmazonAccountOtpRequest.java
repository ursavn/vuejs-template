package com.ursa.tools.amazon.payload;

import lombok.Data;

@Data
public class AmazonAccountOtpRequest {
	private String email;
	private String code;
}
