package com.ursa.tools.amazon.model.page.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderData {
	private boolean success;
	private String message;
}
