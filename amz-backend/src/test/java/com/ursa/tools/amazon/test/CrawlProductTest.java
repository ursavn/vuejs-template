package com.ursa.tools.amazon.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ursa.tools.amazon.constant.enums.QuantityOption;
import com.ursa.tools.amazon.constant.enums.SellerOption;
import com.ursa.tools.amazon.model.Automation;
import com.ursa.tools.amazon.model.Product;
import com.ursa.tools.amazon.model.ProductSeller;
import com.ursa.tools.amazon.model.Seller;
import com.ursa.tools.amazon.model.page.data.ProductData;
import com.ursa.tools.amazon.model.page.data.SellerData;
import com.ursa.tools.amazon.service.AmazonService;
import com.ursa.tools.amazon.service.AutomationService;
import com.ursa.tools.amazon.service.ProductSellerService;
import com.ursa.tools.amazon.service.ProductService;
import com.ursa.tools.amazon.service.SellerService;
import com.ursa.tools.amazon.test.util.CrawlProductAsyncTask;

@SpringBootTest
public class CrawlProductTest {
	
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	ProductService productService;
	
	@Autowired
	ProductSellerService productSellerService;
	
	@Autowired
	SellerService sellerService;	
	
	@Autowired
	AmazonService amazonService;
	
	@Autowired
	AutomationService automationService;
	
	@Autowired
	CrawlProductAsyncTask crawlProductAsyncTask;
	
	//@Test
	public void test() {
		String path = "C:/Users/tavan/Downloads/amz-tool";	
		long beginTime = System.currentTimeMillis();
		try {
			BufferedReader br = new BufferedReader(new FileReader(path + "/input.txt"));			
			List<String> asins = new ArrayList<String>();
			Map<String, Long> buyPrices = new HashMap<>(); 
			String line = "";
			while((line = br.readLine()) != null) {
				String[] arr = line.split(" ");
				asins.add(arr[0].trim());
				buyPrices.put(arr[0].trim(), Long.parseLong(arr[1].trim()));
			}				
			br.close();

			List<CompletableFuture<ProductData>> tasks = asins.stream()
					.map(asin -> crawlProductAsyncTask.execute(asin))
					.collect(Collectors.toList());			
			CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();	
			
			FileWriter fileWriter = new FileWriter(path + "/output.txt");
			for(CompletableFuture<ProductData> task : tasks) {
				ProductData productData = task.get();
				if(productData.getPrice() == null) {
					 fileWriter.write(productData.getAsin() + "\n");
				} else {
					Product product = new Product();
			    	product.setAsin(productData.getAsin());
			    	product.setName(productData.getName());
			    	product.setPrice(productData.getPrice());
			    	product.setImage(productData.getImage());
			    	product.setRate(productData.getRate());
			    	product = productService.save(product);
			    	//Product Seller
			    	if(productData.getSellers() != null) {
				    	for(SellerData sellerData : productData.getSellers()) {
			    			Seller seller = sellerService.findByName(sellerData.getSellerName());
					    	if(seller == null) {
					    		seller = sellerService.save(new Seller(sellerData.getSellerName()));
					    	}
					    	ProductSeller productSeller = new ProductSeller();
					    	productSeller.setProduct(product);
					    	productSeller.setSeller(seller);
					    	productSeller.setPrice(sellerData.getPrice());
					    	productSeller = productSellerService.save(productSeller);
			    		}
			    	}	   
			    	
			    	Automation automation = new Automation();
					automation.setStatus(false);
					automation.setProduct(product);
					automation.setFromTime(0);
					automation.setToTime(1440);
					automation.setPrimeOnly(false);	
					automation.setSellerOption(SellerOption.ALL);
					automation.setSellerOptionValue(null);
					automation.setPrice(buyPrices.get(productData.getAsin()));
					automation.setPriceOption(null);
					automation.setQuantity(1);
					automation.setQuantityOption(QuantityOption.MAX_QUANTITY);
					automation.setQuantityOptionValue(1);
					automationService.save(automation);
				}
			}
			
			fileWriter.write("Crawl " + tasks.size() + " product on " + ((System.currentTimeMillis() - beginTime) / 1000) + "s");
			fileWriter.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
