package com.ursa.tools.amazon.service;

import java.util.List;

import com.ursa.tools.amazon.model.ProductSeller;

public interface ProductSellerService {
	List<ProductSeller> findByProductIdAndSellerId(Integer productId, Integer sellerId);
    ProductSeller save(ProductSeller productSeller);
}
