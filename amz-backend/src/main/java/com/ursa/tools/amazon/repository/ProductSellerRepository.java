package com.ursa.tools.amazon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ursa.tools.amazon.model.ProductSeller;

@Repository
public interface ProductSellerRepository extends JpaRepository<ProductSeller, Integer> {
	List<ProductSeller> findByProductIdAndSellerId(Integer productId, Integer sellerId);
}
