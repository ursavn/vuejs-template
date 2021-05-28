package com.ursa.tools.amazon.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ursa.tools.amazon.model.AmazonAccount;
import com.ursa.tools.amazon.model.Product;
import com.ursa.tools.amazon.model.ProductSeller;
import com.ursa.tools.amazon.model.Seller;
import com.ursa.tools.amazon.model.page.data.CrawlProductData;
import com.ursa.tools.amazon.model.page.data.ProductData;
import com.ursa.tools.amazon.model.page.data.SellerData;
import com.ursa.tools.amazon.payload.ProductResponse;
import com.ursa.tools.amazon.service.AmazonAccountService;
import com.ursa.tools.amazon.service.AmazonService;
import com.ursa.tools.amazon.service.ProductSellerService;
import com.ursa.tools.amazon.service.ProductService;
import com.ursa.tools.amazon.service.SellerService;

@RestController
public class ProductController {

	@Autowired
	ProductService productService;
	
	@Autowired
	ProductSellerService productSellerService;
	
	@Autowired
	SellerService sellerService;
	
	@Autowired
	AmazonService amazonService;
	
	@Autowired
	AmazonAccountService amazonAccountService;
	
	@GetMapping("/products/id/{id}")
	public ResponseEntity<ProductResponse> findProductById(@PathVariable Integer id) throws Exception {
		Product product = null;
		Optional<Product> productOptional = productService.findById(id);
		if(productOptional.isPresent()) {
			product = productOptional.get();
			ProductResponse response = ProductResponse.mapper(product);	    	
	    	return new ResponseEntity<>(response, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);		
	}
	
	@GetMapping("/products/{asin}")
	public ResponseEntity<ProductResponse> findProductByAsin(@PathVariable String asin) throws Exception {
		Product product = null;
		AmazonAccount account = null;
		Optional<Product> productOptional = productService.findByAsin(asin);
		if(productOptional.isPresent()) {
			product = productOptional.get();			
		}
		Optional<AmazonAccount> optionalAccount = amazonAccountService.getRandomAccount();
		if(optionalAccount.isPresent()) {
			account = optionalAccount.get();
		}

		WebDriver driver = amazonService.initWebDriver(account);	
		CrawlProductData crawlProductData = amazonService.crawlProduct(driver, account, asin);	
	    driver.quit();
	    if(crawlProductData.getData() != null) {
	    	ProductData productData = crawlProductData.getData();
	    	if(product == null) 
	    		product = new Product();
	    	product.setAsin(asin);
	    	product.setName(productData.getName());
	    	product.setPrice(productData.getPrice());
	    	product.setImage(productData.getImage());
	    	product.setRate(productData.getRate());
	    	product = productService.save(product);
	    	//Product Seller
	    	if(productData.getSellers() != null) {
		    	List<ProductSeller> productSellers = new ArrayList<ProductSeller>();
		    	for(SellerData sellerData : productData.getSellers()) {
	    			Seller seller = sellerService.findByName(sellerData.getSellerName());
			    	if(seller == null) {
			    		seller = sellerService.save(new Seller(sellerData.getSellerName()));
			    	}		
			    	List<ProductSeller> productSellerExists = productSellerService.findByProductIdAndSellerId(product.getId(), seller.getId());
			    	if(productSellerExists.size() == 0) {
			    		ProductSeller productSeller = new ProductSeller();
			    	    productSeller.setProduct(product);
				    	productSeller.setSeller(seller);
				    	productSeller.setPrice(sellerData.getPrice());
				    	productSeller = productSellerService.save(productSeller);
				    	productSellers.add(productSeller);
			    	} else {
			    		productSellers.add(productSellerExists.get(0));
			    	}
	    		}
		    	product.setProductSellers(productSellers);
	    	}	    	
	    }
	    if(product != null) {	    
	    	ProductResponse response = ProductResponse.mapper(product);	    	
	    	return new ResponseEntity<>(response, HttpStatus.OK);
	    }
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);		
	}
}
