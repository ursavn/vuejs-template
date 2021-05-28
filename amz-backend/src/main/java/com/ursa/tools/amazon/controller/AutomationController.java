package com.ursa.tools.amazon.controller;

import java.util.Optional;

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

import com.ursa.tools.amazon.model.Automation;
import com.ursa.tools.amazon.model.AutomationLog;
import com.ursa.tools.amazon.model.Product;
import com.ursa.tools.amazon.payload.AutomationAddRequest;
import com.ursa.tools.amazon.payload.AutomationEditRequest;
import com.ursa.tools.amazon.payload.AutomationListRequest;
import com.ursa.tools.amazon.payload.AutomationStatusRequest;
import com.ursa.tools.amazon.payload.ListRequest;
import com.ursa.tools.amazon.payload.ListResponse;
import com.ursa.tools.amazon.payload.MessageResponse;
import com.ursa.tools.amazon.security.SecurityUtil;
import com.ursa.tools.amazon.service.AutomationLogService;
import com.ursa.tools.amazon.service.AutomationService;
import com.ursa.tools.amazon.service.ProductService;

@RestController
public class AutomationController {
	
	@Autowired
	AutomationService automationService;
	
	@Autowired
	AutomationLogService automationLogService;
	
	@Autowired
	ProductService productService;

	@GetMapping("/automations")
	public ResponseEntity<?> listAutomation(@ModelAttribute AutomationListRequest request) {
		int userId = SecurityUtil.getAuthenticationUserId();
		Sort sort = request.getSortDesc() ? Sort.by(request.getSortBy()).descending() : Sort.by(request.getSortBy()).ascending();
		Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
		Page<Automation> pageAutomation = automationService.search(request.getSearch(), request.getStatus(), userId, pageable);
		return new ResponseEntity<>(new ListResponse(pageAutomation), HttpStatus.OK);
	}
	
	@GetMapping("/automations/{id}")
	public ResponseEntity<?> getAutomation(@PathVariable Integer id) {
		int userId = SecurityUtil.getAuthenticationUserId();
		Optional<Automation> optional = automationService.findById(id);	
		if(!optional.isPresent() || optional.get().getCreatedBy() != userId) {
			return ResponseEntity.badRequest().body(new MessageResponse("Not found"));
		}
		return new ResponseEntity<>(optional.get(), HttpStatus.OK);		
	}
	
	@GetMapping("/automations/{id}/logs")
	public ResponseEntity<?> getAutomationLog(@PathVariable Integer id, @ModelAttribute ListRequest request) {
		int userId = SecurityUtil.getAuthenticationUserId();
		Optional<Automation> optional = automationService.findById(id);	
		if(!optional.isPresent() || optional.get().getCreatedBy() != userId) {
			return ResponseEntity.badRequest().body(new MessageResponse("Not found"));
		}
		
		Sort sort = request.getSortDesc() ? Sort.by(request.getSortBy()).descending() : Sort.by(request.getSortBy()).descending();
		Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
		Page<AutomationLog> pageAutomationLog = automationLogService.findByAutomationId(id, pageable);
		return new ResponseEntity<>(new ListResponse(pageAutomationLog), HttpStatus.OK);	
	}
	
	@PostMapping("/automations")
	public ResponseEntity<?> addAutomation(@RequestBody AutomationAddRequest request) {
		Optional<Product> optionalProduct = productService.findById(request.getProductId());
		Product product = optionalProduct.get();
		
		Automation automation = new Automation();
		automation.setStatus(request.getStatus());
		automation.setProduct(product);
		automation.setFromTime(request.getFromTime());
		automation.setToTime(request.getToTime());
		automation.setPrimeOnly(request.getPrimeOnly());	
		automation.setSellerOption(request.getSellerOption());
		automation.setSellerOptionValue(request.getSellerOptionValue());
		automation.setPrice(request.getPrice());
		automation.setPriceOption(request.getPriceOption());
		automation.setQuantity(request.getQuantity());
		automation.setQuantityOption(request.getQuantityOption());
		automation.setQuantityOptionValue(request.getQuantityOptionValue());
		automation.setPurchaseQuantity(0);
		automation.setAccounts(request.getAccounts());		
		automationService.save(automation);
		return new ResponseEntity<>(automation, HttpStatus.OK);
	}
	
	@PostMapping("/automations/{id}")
	public ResponseEntity<?> updateAutomation(@PathVariable Integer id, @RequestBody AutomationEditRequest request) {
		int userId = SecurityUtil.getAuthenticationUserId();
		Optional<Automation> optional = automationService.findById(id);	
		if(!optional.isPresent() || optional.get().getCreatedBy() != userId) {
			return ResponseEntity.badRequest().body(new MessageResponse("Not found"));
		}
		
		Automation automation = optional.get();
		automation.setStatus(request.getStatus());
		automation.setFromTime(request.getFromTime());
		automation.setToTime(request.getToTime());
		automation.setPrimeOnly(request.getPrimeOnly());	
		automation.setSellerOption(request.getSellerOption());
		automation.setSellerOptionValue(request.getSellerOptionValue());
		automation.setPrice(request.getPrice());
		automation.setPriceOption(request.getPriceOption());
		automation.setQuantity(request.getQuantity());
		automation.setQuantityOption(request.getQuantityOption());
		automation.setQuantityOptionValue(request.getQuantityOptionValue());
		automation.setAccounts(request.getAccounts());
		automationService.save(automation);
		return new ResponseEntity<>(automation, HttpStatus.OK);		
	}
	
	@DeleteMapping("/automations/{id}")
	public ResponseEntity<?> deleteAutomation(@PathVariable Integer id) {
		int userId = SecurityUtil.getAuthenticationUserId();
		Optional<Automation> optional = automationService.findById(id);	
		if(!optional.isPresent() || optional.get().getCreatedBy() != userId) {
			return ResponseEntity.badRequest().body(new MessageResponse("Not found"));
		}
		automationService.delete(optional.get());
		return ResponseEntity.ok(new MessageResponse("Delete automation successfully!"));	
	}
	
	@PostMapping("/automations/{id}/status")
	public ResponseEntity<?> changeStatusAutomation(@PathVariable Integer id, @RequestBody AutomationStatusRequest request) {
		int userId = SecurityUtil.getAuthenticationUserId();
		Optional<Automation> optional = automationService.findById(id);	
		if(!optional.isPresent() || optional.get().getCreatedBy() != userId) {
			return ResponseEntity.badRequest().body(new MessageResponse("Not found"));
		}
		Automation automation = optional.get();
		automation.setStatus(request.isStatus());
		automationService.save(automation);
		return ResponseEntity.ok(new MessageResponse("Change automation status successfully!"));	
	}
}
