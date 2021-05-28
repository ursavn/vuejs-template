package com.ursa.tools.amazon.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ursa.tools.amazon.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	User findByEmail(String email);	
	Boolean existsByEmail(String email);
	
	@Query("SELECT u FROM User u WHERE :search IS NULL OR fullName LIKE CONCAT('%',:search,'%') OR email like CONCAT('%',:search,'%')")
	Page<User> search(@Param("search") String search, Pageable pageable);
}
