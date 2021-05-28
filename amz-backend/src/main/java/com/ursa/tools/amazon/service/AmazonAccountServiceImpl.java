package com.ursa.tools.amazon.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ursa.tools.amazon.constant.enums.AmazonAccountStatus;
import com.ursa.tools.amazon.model.AmazonAccount;
import com.ursa.tools.amazon.repository.AmazonAccountRepository;

@Service
public class AmazonAccountServiceImpl implements AmazonAccountService {

	@Autowired
	AmazonAccountRepository amazonAccountRepository;
	
	@Override
	public Page<AmazonAccount> search(String search, AmazonAccountStatus status, Integer createdBy, Pageable pageable) {
		return amazonAccountRepository.search(search, status, createdBy, pageable);
	}

	@Override
	public Optional<AmazonAccount> findById(Integer id) {
		return amazonAccountRepository.findById(id);
	}
	
	@Override
	public List<AmazonAccount> findByIds(String ids) {
		if (ids != null && !"".equals(ids.trim())) {
			try {
				List<Integer> listId = Arrays.asList(ids.split(",")).stream()
						.map(Integer::parseInt)
						.collect(Collectors.toList());
				return amazonAccountRepository.findByIdIn(listId);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Optional<AmazonAccount> findByEmail(String email) {
		return amazonAccountRepository.findByEmail(email);
	}
	
	@Override
	public Page<AmazonAccount> findByStatus(AmazonAccountStatus status, Pageable pageable) {
		return amazonAccountRepository.findByStatus(status, pageable);
	}

	@Override
	@Transactional
	public AmazonAccount save(AmazonAccount amazonAccount) {
		return amazonAccountRepository.save(amazonAccount);
	}

	@Override
	@Transactional
	public void delete(AmazonAccount amazonAccount) {
		amazonAccountRepository.delete(amazonAccount);
	}

	@Override
	public Optional<AmazonAccount> getRandomAccount() {		
		Page<AmazonAccount> accounts = amazonAccountRepository.findByStatus(AmazonAccountStatus.ACTIVE, PageRequest.of(0, Integer.MAX_VALUE));		
		if(accounts.hasContent()) {
			long total = accounts.getTotalElements();
			int offset = new Random().nextInt((int)total);
			return Optional.of(accounts.getContent().get(offset));
		}
		return Optional.empty();
	}
}
