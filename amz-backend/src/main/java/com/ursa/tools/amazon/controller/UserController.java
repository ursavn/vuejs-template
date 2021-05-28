package com.ursa.tools.amazon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ursa.tools.amazon.constant.enums.UserRole;
import com.ursa.tools.amazon.model.User;
import com.ursa.tools.amazon.payload.ListRequest;
import com.ursa.tools.amazon.payload.ListResponse;
import com.ursa.tools.amazon.payload.MessageResponse;
import com.ursa.tools.amazon.payload.SignupRequest;
import com.ursa.tools.amazon.service.UserService;

@RestController
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	PasswordEncoder encoder;

	@GetMapping("/users")
	public ResponseEntity<?> listUser(@ModelAttribute ListRequest request) {	
		Sort sort = request.getSortDesc() ? Sort.by(request.getSortBy()).descending() : Sort.by(request.getSortBy()).ascending();
		Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
		Page<User> pageUsers = userService.search(request.getSearch(), pageable);
		return new ResponseEntity<>(new ListResponse(pageUsers), HttpStatus.OK);
	}
	
	@PostMapping("/users")
	public ResponseEntity<?> addUser(@RequestBody SignupRequest signUpRequest) {
		if (userService.existsByUsername(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already taken!"));
		}
		// Create new user's account
		User user = new User();
		user.setEmail(signUpRequest.getEmail()); 
		user.setPassword(encoder.encode(signUpRequest.getPassword()));
		user.setFullName(signUpRequest.getFullName());
		user.setRole(UserRole.USER);
		userService.save(user);

		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	@PostMapping("/users/{id}")
	public ResponseEntity<?> updateUser(@PathVariable("id") Integer id, @RequestBody SignupRequest request) {
		User user = userService.findById(id);
		if(user != null) {
			user.setFullName(request.getFullName());
			userService.save(user);
			return new ResponseEntity<>(user, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/users/{id}")
	public ResponseEntity<?> getUser(@PathVariable("id") Integer id) {
		User user = userService.findById(id);
		if(user != null) {
			return ResponseEntity.ok(user);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping("/users/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable("id") Integer id) {
		User user = userService.findById(id);
		if(user != null && user.getRole().equals(UserRole.USER)) {
			userService.delete(user);
			return ResponseEntity.ok(new MessageResponse("Delete user successfully!"));
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
