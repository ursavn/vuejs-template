package com.ursa.tools.amazon.service;

import java.util.Optional;

import com.ursa.tools.amazon.model.Product;

public interface ProductService {
	Optional<Product> findById(Integer id);
	Optional<Product> findByAsin(String asin);
	Product save(Product product);	
}