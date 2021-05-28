package com.ursa.tools.amazon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ursa.tools.amazon.model.Seller;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer> {
    Seller findByName(String name);
}
