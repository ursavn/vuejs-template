package com.ursa.tools.amazon.controller;

import java.util.Calendar;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ursa.tools.amazon.model.PasswordResetToken;
import com.ursa.tools.amazon.model.User;
import com.ursa.tools.amazon.payload.ChangePasswordRequest;
import com.ursa.tools.amazon.payload.ForgotPasswordRequest;
import com.ursa.tools.amazon.payload.LoginRequest;
import com.ursa.tools.amazon.payload.LoginResponse;
import com.ursa.tools.amazon.payload.MessageResponse;
import com.ursa.tools.amazon.payload.SignupRequest;
import com.ursa.tools.amazon.security.JwtUtils;
import com.ursa.tools.amazon.security.UserDetailsImpl;
import com.ursa.tools.amazon.service.EmailService;
import com.ursa.tools.amazon.service.ForgotPasswordService;
import com.ursa.tools.amazon.service.UserService;

@RestController
public class AuthController {
	
	Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserService userService;
	
	@Autowired
	ForgotPasswordService forgotPasswordService;
	
	@Autowired
	EmailService emailService;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;
	
	@Value("${server.base-url}")
	String baseUrl;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		

		return ResponseEntity.ok(new LoginResponse(jwt,
				userDetails.getUser().getId(), 
				userDetails.getUser().getEmail(), 
				userDetails.getUser().getFullName(),
				userDetails.getUser().getRole()));
	}
	
	//@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
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
		userService.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
	@GetMapping("/profile")
	public ResponseEntity<?> profile() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User userLogin = userService.findByEmail(authentication.getName());
		return ResponseEntity.ok(userLogin);
	}
	
	@PostMapping("/change-password")
	public ResponseEntity<?> changePassword(@Validated @RequestBody ChangePasswordRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User userLogin = userService.findByEmail(authentication.getName());
		if(encoder.matches(request.getOldPassword(), userLogin.getPassword())) {
			userLogin.setPassword(encoder.encode(request.getNewPassword()));
			userService.update(userLogin);
			return ResponseEntity.ok(new MessageResponse("Change password successfully!"));
		}
		return ResponseEntity.ok(new MessageResponse("Old password not true!"));		
	}
	
	@PostMapping("/forgot-password/token")
	public ResponseEntity<?> forgotPasswordToken(@RequestBody ForgotPasswordRequest request) {
		User user = userService.findByEmail(request.getEmail());
	    if (user == null) {
	    	return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email not existed"));
	    }
		String token = UUID.randomUUID().toString();
		forgotPasswordService.save(new PasswordResetToken(token, user));
		//send email
		String subject = "Confirm reset password";
		String text = "Click link to confirm reset password: \n " + 
		              baseUrl + "/forgot-password/confirm?token=" + token;
		emailService.sendSimpleMessage(request.getEmail(), subject, text);
		
		return ResponseEntity.ok(new MessageResponse("Token sent to email"));
	}
	
	@PostMapping("/forgot-password/confirm")
	public ResponseEntity<?> forgotPasswordConfirm(@RequestBody ForgotPasswordRequest request) {
		if(request.getToken() == null || request.getToken().equals("")) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Token not found"));
		}
		if(request.getPassword() == null || request.getPassword().trim().equals("")) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Password not found"));
		}
		PasswordResetToken passToken = forgotPasswordService.findByToken(request.getToken());
		if(passToken == null) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Invalid token"));	    
		}
		Calendar cal = Calendar.getInstance();
        if (passToken.getExpiredDate().before(cal.getTime())) {
        	return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Token expired"));	    
        }
		User user = passToken.getUser();
		user.setPassword(encoder.encode(request.getPassword()));
		userService.save(user);
		return ResponseEntity.ok(new MessageResponse("Change password successful!"));
	}
}
