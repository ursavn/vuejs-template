package com.ursa.tools.amazon.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ursa.tools.amazon.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
   
	@Query("select o from Order o where userId = :userId and " + 
	        "(:search is null or o.product.asin like concat(:search,'%') " + 
	        "or o.product.name like concat('%',:search,'%') or o.sellerName like concat('%',:search,'%') " + 
		    "or o.amazonAccountEmail like concat('%',:search,'%')) ")
	Page<Order> search(@Param("search") String search, @Param("userId") Integer userId, Pageable pageable);
}
