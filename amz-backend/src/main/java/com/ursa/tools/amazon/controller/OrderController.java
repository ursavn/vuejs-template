package com.ursa.tools.amazon.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ursa.tools.amazon.model.Order;
import com.ursa.tools.amazon.payload.ListRequest;
import com.ursa.tools.amazon.payload.ListResponse;
import com.ursa.tools.amazon.security.SecurityUtil;
import com.ursa.tools.amazon.service.OrderService;

@RestController
public class OrderController {

	@Autowired
	OrderService orderService;
	
	@GetMapping("/orders")
	public ResponseEntity<?> listOrder(@ModelAttribute ListRequest request) {
		Integer userId = SecurityUtil.getAuthenticationUserId();
		Sort sort = request.getSortDesc() ? Sort.by(request.getSortBy()).descending() : Sort.by(request.getSortBy()).ascending();
		Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
		Page<Order> pageOrder = orderService.search(request.getSearch(), userId, pageable);
		return new ResponseEntity<>(new ListResponse(pageOrder), HttpStatus.OK);
	}
	
	@GetMapping("/orders/{id}")
	public ResponseEntity<?> getOrder(@PathVariable Integer id) {
		int userId = SecurityUtil.getAuthenticationUserId();
		Optional<Order> optionalOrder = orderService.findById(id);
		if(!optionalOrder.isPresent() || optionalOrder.get().getUserId() != userId) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(optionalOrder.get(), HttpStatus.OK);
	}
}
