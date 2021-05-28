package com.ursa.tools.amazon.model.page.data;

import com.ursa.tools.amazon.constant.enums.ProductCouponDataType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductCouponData {
    private Long coupon;
    private ProductCouponDataType type;
}
