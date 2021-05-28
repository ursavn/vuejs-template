package com.ursa.tools.amazon.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ursa.tools.amazon.constant.enums.AmazonAccountStatus;
import com.ursa.tools.amazon.model.AmazonAccount;

@Repository
public interface AmazonAccountRepository extends JpaRepository<AmazonAccount, Integer> {
	Optional<AmazonAccount> findByEmail(String email);

	Page<AmazonAccount> findByStatus(AmazonAccountStatus status, Pageable pageable);

	@Query("select account from AmazonAccount account where createdBy = :createdBy "
			+ "and (:search is null or email like concat('%',:search,'%')) "
			+ "and (:status is null or status = :status)")
	Page<AmazonAccount> search(@Param("search") String search, @Param("status") AmazonAccountStatus status,
			@Param("createdBy") Integer createdBy, Pageable pageable);

	List<AmazonAccount> findByIdIn(List<Integer> ids);
}
