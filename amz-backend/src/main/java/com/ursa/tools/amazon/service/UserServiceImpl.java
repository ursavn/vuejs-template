package com.ursa.tools.amazon.service;

import java.util.Optional;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ursa.tools.amazon.model.User;
import com.ursa.tools.amazon.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public Page<User> search(String search, Pageable pageable) {		
		return userRepository.search(search, pageable);
	}
	
	@Override
	public User findById(Integer id) {
		Optional<User> optional = userRepository.findById(id);
		if(optional.isPresent())
			return optional.get();					
		return null;
	}

	@Override
	@Transactional(value = TxType.REQUIRES_NEW)
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public boolean existsByUsername(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	@Transactional
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	@Transactional
	public User update(User user) {
		return userRepository.save(user);
	}

	@Override
	@Transactional
	public void delete(User user) {
		userRepository.delete(user);
	}
	
}
