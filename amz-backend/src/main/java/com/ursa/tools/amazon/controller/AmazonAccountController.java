package com.ursa.tools.amazon.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ursa.tools.amazon.constant.enums.AmazonAccountStatus;
import com.ursa.tools.amazon.constant.enums.LoginAmazonStatus;
import com.ursa.tools.amazon.model.AmazonAccount;
import com.ursa.tools.amazon.model.page.data.LoginData;
import com.ursa.tools.amazon.payload.AmazonAccountAddRequest;
import com.ursa.tools.amazon.payload.AmazonAccountListRequest;
import com.ursa.tools.amazon.payload.AmazonAccountOtpRequest;
import com.ursa.tools.amazon.payload.ListResponse;
import com.ursa.tools.amazon.payload.MessageResponse;
import com.ursa.tools.amazon.security.SecurityUtil;
import com.ursa.tools.amazon.service.AmazonAccountService;
import com.ursa.tools.amazon.service.AmazonService;
import com.ursa.tools.amazon.util.WebDriverItem;
import com.ursa.tools.amazon.util.WebDriverPool;

@RestController
public class AmazonAccountController {
	
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	AmazonAccountService amazonAccountService;
	
	@Autowired
	AmazonService amazonService;
	
	@GetMapping("/amazon")
	public ResponseEntity<ListResponse> listAccount(@ModelAttribute AmazonAccountListRequest request) {
		Integer userId = SecurityUtil.getAuthenticationUserId();		
		Sort sort = request.getSortDesc() ? Sort.by(request.getSortBy()).descending() : Sort.by(request.getSortBy()).descending();
		Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
		Page<AmazonAccount> pageAccount = amazonAccountService.search(request.getSearch(), request.getStatus(), userId, pageable);
		return new ResponseEntity<>(new ListResponse(pageAccount), HttpStatus.OK);
	}
	
	@PostMapping("/amazon")
	public ResponseEntity<?> addAccount(@RequestBody AmazonAccountAddRequest request) throws Exception {
		Optional<AmazonAccount> accountOptional = amazonAccountService.findByEmail(request.getEmail());
		if(accountOptional.isPresent()) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is existed!"));
		} 
		LoginData loginData = amazonService.loginAmazon(request.getEmail(), request.getPassword());
		if(loginData.getStatus().equals(LoginAmazonStatus.VERIFY)
				|| loginData.getStatus().equals(LoginAmazonStatus.SUCCESS)) {
		    AmazonAccount account = new AmazonAccount();
		    account.setEmail(request.getEmail());
		    account.setPassword(request.getPassword());
		    if(loginData.getMessage() != null) {
			    account.setStatus(AmazonAccountStatus.ACTIVE);
			    account.setCookies(loginData.getMessage());
		    } else {
		    	account.setStatus(AmazonAccountStatus.NOT_VERIFY);
		    }
		    amazonAccountService.save(account);
		    loginData.setMessage(account.getId() + "");
		} 
		return new ResponseEntity<>(loginData, HttpStatus.OK);
	}
	
	@PostMapping("/amazon/{id}/relogin")
	public ResponseEntity<?> reloginAccount(@PathVariable("id") Integer id) throws Exception {
		Optional<AmazonAccount> accountOptional = amazonAccountService.findById(id);
		if(accountOptional.isPresent()) {
			AmazonAccount account = accountOptional.get();
			LoginData loginData = amazonService.loginAmazon(account.getEmail(), account.getPassword());
			if(loginData.getStatus().equals(LoginAmazonStatus.SUCCESS)) {
			    account.setStatus(AmazonAccountStatus.ACTIVE);
			    account.setCookies(loginData.getMessage());
			    amazonAccountService.save(account);			    
			} 
			if(loginData.getStatus().equals(LoginAmazonStatus.VERIFY)
					|| loginData.getStatus().equals(LoginAmazonStatus.SUCCESS)) {
				loginData.setMessage(account.getId() + "");
			}
			return new ResponseEntity<>(loginData, HttpStatus.OK);
		}
		return ResponseEntity.badRequest().body(new MessageResponse("Not found"));		
	}
	
	@PostMapping("/amazon/otp")
	public ResponseEntity<?> sendOtp(@RequestBody AmazonAccountOtpRequest request) throws Exception {
		WebDriverPool driverPool = WebDriverPool.getInstance();
		WebDriverItem webDriverItem = driverPool.getDriver(request.getEmail());
		
		LoginData loginData = amazonService.sendOtp(webDriverItem, request.getCode());
		if(loginData.getStatus().equals(LoginAmazonStatus.VERIFY)
				|| loginData.getStatus().equals(LoginAmazonStatus.SUCCESS)) {
			AmazonAccount account = null;
		    Optional<AmazonAccount> accountOptional = amazonAccountService.findByEmail(request.getEmail());
			if(accountOptional.isPresent()) {
				account = accountOptional.get();
			} else {
				account = new AmazonAccount();
			}
		    account.setEmail(request.getEmail());
		    account.setPassword(webDriverItem.getPassword());
		    if(loginData.getMessage() != null) {
			    account.setStatus(AmazonAccountStatus.ACTIVE);
			    account.setCookies(loginData.getMessage());
		    } else {
		    	account.setStatus(AmazonAccountStatus.NOT_VERIFY);
		    }
		    amazonAccountService.save(account);
		    loginData.setMessage(account.getId() + "");
		} 
		return new ResponseEntity<>(loginData, HttpStatus.OK);
	}
	
	@PostMapping("/amazon/captcha")
	public ResponseEntity<?> sendCaptcha(@RequestBody AmazonAccountOtpRequest request) throws Exception {
		WebDriverPool driverPool = WebDriverPool.getInstance();
		WebDriverItem webDriverItem = driverPool.getDriver(request.getEmail());
		
		LoginData loginData = amazonService.sendCaptcha(webDriverItem, request.getCode());
		if(loginData.getStatus().equals(LoginAmazonStatus.VERIFY)
				|| loginData.getStatus().equals(LoginAmazonStatus.SUCCESS)) {
		    AmazonAccount account = null;
		    Optional<AmazonAccount> accountOptional = amazonAccountService.findByEmail(request.getEmail());
			if(accountOptional.isPresent()) {
				account = accountOptional.get();
			} else {
				account = new AmazonAccount();
			}
		    account.setEmail(request.getEmail());
		    account.setPassword(webDriverItem.getPassword());
		    if(loginData.getMessage() != null) {
			    account.setStatus(AmazonAccountStatus.ACTIVE);
			    account.setCookies(loginData.getMessage());
		    } else {
		    	account.setStatus(AmazonAccountStatus.NOT_VERIFY);
		    }
		    amazonAccountService.save(account);
		    loginData.setMessage(account.getId() + "");
		} 
		return new ResponseEntity<>(loginData, HttpStatus.OK);
	}
	
	@GetMapping("/amazon/{id}")
	public ResponseEntity<?> getAccount(@PathVariable("id") Integer id) {
		int userId = SecurityUtil.getAuthenticationUserId();
		Optional<AmazonAccount> accountOptional = amazonAccountService.findById(id);
		if(!accountOptional.isPresent()) {
			return ResponseEntity.badRequest().body(new MessageResponse("Not found"));
		}
		AmazonAccount account = accountOptional.get();
		if(account.getCreatedBy() != userId) {
			return ResponseEntity.badRequest().body(new MessageResponse("Not found"));
		}
		
		return new ResponseEntity<>(account, HttpStatus.OK);
	}
	
	@DeleteMapping("/amazon/{id}")
	public ResponseEntity<?> deleteAccount(@PathVariable("id") Integer id) {
		int userId = SecurityUtil.getAuthenticationUserId();
		Optional<AmazonAccount> accountOptional = amazonAccountService.findById(id);
		if(!accountOptional.isPresent()) {
			return ResponseEntity.badRequest().body(new MessageResponse("Not found"));
		}
		AmazonAccount account = accountOptional.get();
		if(account.getCreatedBy() != userId) {
			return ResponseEntity.badRequest().body(new MessageResponse("Not found"));
		}
		
		amazonAccountService.delete(account);
		return ResponseEntity.ok(new MessageResponse("Delete amazon account successfully!"));
	}
}
