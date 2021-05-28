package com.ursa.tools.amazon.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ursa.tools.amazon.constant.enums.AmazonAccountStatus;
import com.ursa.tools.amazon.model.AmazonAccount;

public interface AmazonAccountService {	
    public Page<AmazonAccount> search(String search, AmazonAccountStatus status, Integer createdBy, Pageable pageable);
    public Page<AmazonAccount> findByStatus(AmazonAccountStatus status, Pageable pageable);
    public Optional<AmazonAccount> findById(Integer id);
    public Optional<AmazonAccount> findByEmail(String email);
    public List<AmazonAccount> findByIds(String ids);
    public AmazonAccount save(AmazonAccount amazonAccount);   
    public void delete(AmazonAccount amazonAccount);
    public Optional<AmazonAccount> getRandomAccount();
}
