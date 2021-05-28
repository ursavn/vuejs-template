package com.ursa.tools.amazon.payload;

import lombok.Data;

@Data
public class LineTokenRequest {
	private String code;
	private String grantType;
	private String clientId;
	private String clientSecret;
	private String redirectUri;
}
