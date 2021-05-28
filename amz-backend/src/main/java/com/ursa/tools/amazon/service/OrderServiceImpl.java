package com.ursa.tools.amazon.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ursa.tools.amazon.model.Order;
import com.ursa.tools.amazon.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	OrderRepository orderRepository;

	@Override
	public Optional<Order> findById(Integer id) {
		return orderRepository.findById(id);
	}

	@Override
	public Page<Order> search(String search, Integer userId, Pageable pageable) {
		return orderRepository.search(search, userId, pageable);
	}

	@Override
	public Order save(Order order) {
		return orderRepository.save(order);
	}
	
}
