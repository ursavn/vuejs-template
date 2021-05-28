package com.ursa.tools.amazon.payload;

import lombok.Data;
import lombok.NonNull;

@Data
public class AmazonAccountAddRequest {    
	@NonNull
	private String email;
	
	@NonNull
	private String password;
}
