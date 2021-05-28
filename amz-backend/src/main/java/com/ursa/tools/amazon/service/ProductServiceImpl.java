package com.ursa.tools.amazon.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ursa.tools.amazon.model.Product;
import com.ursa.tools.amazon.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	ProductRepository productRepository;

	@Override
	public Optional<Product> findById(Integer id) {
		return productRepository.findById(id);
	}

	@Override
	public Optional<Product> findByAsin(String asin) {
		return productRepository.findByAsin(asin);		
	}

	@Override
	public Product save(Product product) {
		return productRepository.save(product);
	}
}
