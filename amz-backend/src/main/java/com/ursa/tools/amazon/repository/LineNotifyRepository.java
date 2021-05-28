package com.ursa.tools.amazon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ursa.tools.amazon.model.LineNotify;

@Repository
public interface LineNotifyRepository extends JpaRepository<LineNotify, Integer>{
    Optional<LineNotify> findByCreatedBy(Integer createdBy); 
}
