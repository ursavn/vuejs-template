package com.ursa.tools.amazon.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ursa.tools.amazon.model.AmazonAccount;
import com.ursa.tools.amazon.model.Automation;
import com.ursa.tools.amazon.repository.AutomationRepository;

@Service
public class AutomationServiceImpl implements AutomationService {

	@Autowired
	AutomationRepository automationRepository;	

	@Override
	public Page<Automation> search(String search, Boolean status, Integer createdBy, Pageable pageable) {
		return automationRepository.search(search, status, createdBy, pageable);
	}

	@Override
	public Optional<Automation> findById(Integer id) {
		return automationRepository.findById(id);
	}

	@Override
	public Automation save(Automation automation) {
		return automationRepository.save(automation);
	}

	@Override
	public void delete(Automation automation) {
		automationRepository.delete(automation);
	}

	@Override
	public List<Automation> findAutomationExecute(AmazonAccount account) {
		int hour = LocalDateTime.now().getHour();
		int minute = LocalDateTime.now().getMinute();
		int time = hour * 60 + minute;
		return automationRepository.findAutomationExecute(account.getId(), time);
	}

}
