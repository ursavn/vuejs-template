package com.ursa.tools.amazon.payload;

import com.ursa.tools.amazon.constant.enums.LoginAmazonStatus;
import com.ursa.tools.amazon.model.AmazonAccount;

import lombok.Data;

@Data
public class AmazonAccountAddResponse {
	private LoginAmazonStatus status;
	private AmazonAccount account;
	private String message;
}
