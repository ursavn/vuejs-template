package com.ursa.tools.amazon.payload;

import lombok.Data;

@Data
public class ProductSellerResponse {
	private Integer sellerId;
	private String sellerName;
	private long price;
}
