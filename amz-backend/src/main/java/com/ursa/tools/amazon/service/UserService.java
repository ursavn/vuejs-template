package com.ursa.tools.amazon.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ursa.tools.amazon.model.User;

public interface UserService {
   public Page<User> search(String search, Pageable pageable);
   public User findById(Integer id);
   public User findByEmail(String email);
   public boolean existsByUsername(String email);
   public User save(User user);
   public User update(User user);
   public void delete(User user);
}
