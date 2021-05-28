package com.ursa.tools.amazon.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ursa.tools.amazon.model.Automation;

@Repository
public interface AutomationRepository extends JpaRepository<Automation, Integer> {

	@Query("select automation from Automation automation where createdBy = :createdBy " + 
	       "and (:search is null or automation.product.asin like concat(:search,'%') or automation.product.name like concat('%',:search,'%')) " +
		   "and (:status is null or automation.status = :status)")
	Page<Automation> search(@Param("search") String search, @Param("status") Boolean status,
			@Param("createdBy") Integer createdBy, Pageable pageable);

	@Query("select automation from Automation automation "
			+ "where status = true and purchaseQuantity < quantity and fromTime < :time and toTime > :time and concat(',',accounts,',') like concat('%,',:accountId,',%')")
	List<Automation> findAutomationExecute(@Param("accountId") Integer accountId, @Param("time") int time);
}
