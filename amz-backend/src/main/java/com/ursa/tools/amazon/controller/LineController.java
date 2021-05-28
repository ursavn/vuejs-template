package com.ursa.tools.amazon.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ursa.tools.amazon.model.LineNotify;
import com.ursa.tools.amazon.payload.LineNotifyReponse;
import com.ursa.tools.amazon.payload.LineStatusResponse;
import com.ursa.tools.amazon.payload.LineTokenRequest;
import com.ursa.tools.amazon.payload.LineTokenResponse;
import com.ursa.tools.amazon.payload.MessageResponse;
import com.ursa.tools.amazon.security.SecurityUtil;
import com.ursa.tools.amazon.service.LineNotifyService;

@RestController
public class LineController {

	@Autowired
	LineNotifyService lineNotifyService;
	
	@GetMapping("/line")
	public ResponseEntity<?> list() {
		Integer userId = SecurityUtil.getAuthenticationUserId();
		LineNotifyReponse response = null;
		Optional<LineNotify> optional = lineNotifyService.findByCreatedBy(userId);
		if(optional.isPresent()) {
			LineNotify line = optional.get();
			response = LineNotifyReponse.builder()
					.id(line.getId())
					.target(line.getTarget())
					.targetType(line.getTargetType())
					.createdDate(line.getCreatedDate())
					.modifiedDate(line.getModifiedDate())
					.build();
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/line")
	public ResponseEntity<?> update(@Validated @RequestBody LineTokenRequest request) {		
		LineTokenResponse tokenResponse = lineNotifyService.getAccessToken(request);
		if(tokenResponse != null && tokenResponse.getStatus() == 200) {
			String token = tokenResponse.getToken();
			LineStatusResponse lineStatus = lineNotifyService.getStatus(token);
			
			Integer userId = SecurityUtil.getAuthenticationUserId();
			Optional<LineNotify> optional = lineNotifyService.findByCreatedBy(userId);
			LineNotify line = optional.isPresent() ? optional.get() : new LineNotify();
			line.setToken(token);
			if(lineStatus != null && lineStatus.getStatus() == 200) {
				line.setTarget(lineStatus.getTarget());
				line.setTargetType(lineStatus.getTargetType());
			}
			line = lineNotifyService.save(line);
			LineNotifyReponse response = LineNotifyReponse.builder()
					.id(line.getId())
					.target(line.getTarget())
					.targetType(line.getTargetType())
					.createdDate(line.getCreatedDate())
					.modifiedDate(line.getModifiedDate())
					.build();		
	        return new ResponseEntity<>(response, HttpStatus.OK);
		}
		return ResponseEntity.badRequest().body(new MessageResponse("invalid code"));
	}
}
