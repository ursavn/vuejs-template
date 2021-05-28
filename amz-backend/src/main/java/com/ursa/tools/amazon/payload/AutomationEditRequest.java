package com.ursa.tools.amazon.payload;

import com.ursa.tools.amazon.constant.enums.PriceOption;
import com.ursa.tools.amazon.constant.enums.QuantityOption;
import com.ursa.tools.amazon.constant.enums.SellerOption;

import lombok.Data;

@Data
public class AutomationEditRequest {
	private Boolean status;
	private Integer fromTime;
	private Integer toTime;
	private Boolean primeOnly;	
	private SellerOption sellerOption;
	private String sellerOptionValue;
	private Long price;
	private PriceOption priceOption;
	private Integer quantity;
	private QuantityOption quantityOption;
	private Integer quantityOptionValue;
	private String accounts;
}
