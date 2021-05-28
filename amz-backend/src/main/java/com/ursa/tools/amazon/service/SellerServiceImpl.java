package com.ursa.tools.amazon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ursa.tools.amazon.model.Seller;
import com.ursa.tools.amazon.repository.SellerRepository;

@Service
public class SellerServiceImpl implements SellerService {
	
	@Autowired
	SellerRepository sellerRepository;

	@Override
	public Seller findByName(String name) {
		return sellerRepository.findByName(name);
	}

	@Override
	public Seller save(Seller seller) {
		return sellerRepository.save(seller);
	}

}
