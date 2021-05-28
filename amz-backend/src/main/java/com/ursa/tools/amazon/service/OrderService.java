package com.ursa.tools.amazon.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ursa.tools.amazon.model.Order;

public interface OrderService {
    Page<Order> search(String search, Integer userId, Pageable pageable);
    Optional<Order> findById(Integer id);
    Order save(Order order);
}
