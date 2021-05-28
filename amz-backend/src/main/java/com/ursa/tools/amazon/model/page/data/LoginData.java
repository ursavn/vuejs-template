package com.ursa.tools.amazon.model.page.data;

import com.ursa.tools.amazon.constant.enums.LoginAmazonStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginData {
	private LoginAmazonStatus status;
	private String message;
}
