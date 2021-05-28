package com.ursa.tools.amazon.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ursa.tools.amazon.model.AmazonAccount;
import com.ursa.tools.amazon.model.Automation;

public interface AutomationService {
	Page<Automation> search(String search, Boolean status, Integer createdBy, Pageable pageable);
	Optional<Automation> findById(Integer id);
	Automation save(Automation automation);
	void delete(Automation automation);
	List<Automation> findAutomationExecute(AmazonAccount account);
}
