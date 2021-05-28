package com.ursa.tools.amazon.model.page.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ursa.tools.amazon.constant.enums.ProductCouponDataType;
import com.ursa.tools.amazon.constant.enums.SellerOption;
import com.ursa.tools.amazon.model.Automation;

import lombok.Data;

@Data
public class ProductData implements Serializable {
	private String asin;

	private String name;

	private String url;

	private String image;

	private Long price;

	private Double rate;

	private Integer maxQuantity;

	private Long point;

	private ProductCouponData coupon;

	private List<SellerData> sellers;

	public SellerData getSellerByOption(Automation automation, String sellerOptionValue) {
		SellerOption sellerOption = automation.getSellerOption();
		boolean isPrime = automation.getPrimeOnly();
		SellerData sellerData = null;
		List<SellerData> listValidOption = null;

		if (this.getSellers() != null && this.getSellers().size() > 0) {
			switch (sellerOption) {
			case ALL:
				listValidOption = this.getSellers();
				break;

			case AMAZON_ONLY:
				listValidOption = this.getSellers().stream().filter(item -> "Amazon.co.jp".equals(item.getSellerName()))
						.collect(Collectors.toList());
				break;

			case EXCLUDE_SELLER:
				if (sellerOptionValue != null && !sellerOptionValue.equals("")) {
					List<String> excludeList = Arrays.asList(sellerOptionValue.split(","));
					listValidOption = this.getSellers().stream()
							.filter(item -> !excludeList.contains(item.getSellerName())).collect(Collectors.toList());
				} else {
					listValidOption = this.getSellers();
				}
				break;

			case EXCLUDE_RATE_LOWER:
				if (sellerOptionValue != null && this.rate != null) {
					try {
						Double optionValue = Double.parseDouble(sellerOptionValue.trim());
						if (this.rate < optionValue)
							break;
						listValidOption = this.getSellers();
					} catch (Exception e) {
					}
				}
				break;

			default:
				break;
			}
		}
		if (listValidOption != null && listValidOption.size() > 0) {
			if (isPrime) {
				sellerData = listValidOption.stream().filter(item -> item.isPrime()).findAny().orElse(null);
			} else {
				sellerData = listValidOption.get(0);
			}
		}
		return sellerData;
	}

	public Long getCouponPrice(Long sellerPrice) {
		return coupon.getType().equals(ProductCouponDataType.AMOUNT) ? coupon.getCoupon()
				: sellerPrice * coupon.getCoupon() / 100;
	}

	@Override
	public String toString() {
		return "ProductData [asin=" + asin + ", name=" + name + ", url=" + url + ", image=" + image + ", price=" + price
				+ ", rate=" + rate + ", maxQuantity=" + maxQuantity + ", point=" + point + ", coupon=" + coupon
				+ ", sellers=" + sellers + "]";
	}

}
