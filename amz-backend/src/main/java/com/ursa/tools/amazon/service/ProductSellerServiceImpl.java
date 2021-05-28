package com.ursa.tools.amazon.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ursa.tools.amazon.model.ProductSeller;
import com.ursa.tools.amazon.repository.ProductSellerRepository;

@Service
public class ProductSellerServiceImpl implements ProductSellerService {

	@Autowired
	ProductSellerRepository productSellerRepository;
	
	@Override
	public ProductSeller save(ProductSeller productSeller) {
		return productSellerRepository.save(productSeller);
	}

	@Override
	public List<ProductSeller> findByProductIdAndSellerId(Integer productId, Integer sellerId) {	
		return productSellerRepository.findByProductIdAndSellerId(productId, sellerId);
	}

}
