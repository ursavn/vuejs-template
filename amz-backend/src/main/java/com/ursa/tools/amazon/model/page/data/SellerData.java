package com.ursa.tools.amazon.model.page.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerData {
	private Integer index;
	private String sellerName;
	private Long price;
	private boolean isPrime;
}
