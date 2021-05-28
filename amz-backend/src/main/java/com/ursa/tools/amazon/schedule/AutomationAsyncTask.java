package com.ursa.tools.amazon.schedule;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ursa.tools.amazon.constant.enums.AutomationLogType;
import com.ursa.tools.amazon.constant.enums.PriceOption;
import com.ursa.tools.amazon.constant.enums.QuantityOption;
import com.ursa.tools.amazon.model.AmazonAccount;
import com.ursa.tools.amazon.model.Automation;
import com.ursa.tools.amazon.model.AutomationLog;
import com.ursa.tools.amazon.model.Order;
import com.ursa.tools.amazon.model.Product;
import com.ursa.tools.amazon.model.Seller;
import com.ursa.tools.amazon.model.page.data.CrawlProductData;
import com.ursa.tools.amazon.model.page.data.OrderData;
import com.ursa.tools.amazon.model.page.data.ProductData;
import com.ursa.tools.amazon.model.page.data.SellerData;
import com.ursa.tools.amazon.service.AmazonAccountService;
import com.ursa.tools.amazon.service.AmazonService;
import com.ursa.tools.amazon.service.AutomationLogService;
import com.ursa.tools.amazon.service.AutomationService;
import com.ursa.tools.amazon.service.LineNotifyService;
import com.ursa.tools.amazon.service.OrderService;
import com.ursa.tools.amazon.service.ProductService;
import com.ursa.tools.amazon.service.SellerService;
import com.ursa.tools.amazon.util.RandomNumber;

@Component
public class AutomationAsyncTask {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	AmazonService amazonService;

	@Autowired
	AutomationService automationService;

	@Autowired
	AutomationLogService automationLogService;

	@Autowired
	LineNotifyService lineNotifyService;

	@Autowired
	OrderService orderService;

	@Autowired
	ProductService productService;

	@Autowired
	SellerService sellerService;

	@Autowired
	AmazonAccountService amazonAccountService;

	@Async("asyncExecutor")
	public CompletableFuture<AutomationAsyncTaskResult> execute(AmazonAccount account) {		
		WebDriver driver = null;
		int success = 0;
		int error = 0;
		try {			
			List<Automation> automations = automationService.findAutomationExecute(account);
			if(automations.size() == 0) {
				return CompletableFuture.completedFuture(new AutomationAsyncTaskResult(success, error));
			}
			driver = amazonService.initWebDriver(account);
			int n = automations.size();
			int i = 0;
			int taskNumber = account.getId();
			for(Automation automation : automations) {	
				Thread.sleep(RandomNumber.getInt(2, 5)*1000);
				i ++;
				long beginTime = System.currentTimeMillis() / 1000;
				automationLogService.save(new AutomationLog(AutomationLogType.START, "Start", automation));

				Product product = automation.getProduct();
				String asin = product.getAsin();	
				
				CrawlProductData crawlProductData = amazonService.crawlProduct(driver, account, asin);				
				if (crawlProductData.getData() != null) {
					success ++;
					
					ProductData productData = crawlProductData.getData();
					product.setName(productData.getName());
			    	product.setPrice(productData.getPrice());
			    	product.setImage(productData.getImage());
			    	product.setRate(productData.getRate());
			    	product = productService.save(product);
					
					automationLogService.save(
							new AutomationLog(AutomationLogType.NORMAL, "Name: " + productData.getName(), automation));
					// Validate
					SellerData sellerData = productData.getSellerByOption(automation,
							automation.getSellerOptionValue());
					if (sellerData == null) {
						automationLogService.save(new AutomationLog(AutomationLogType.END,
								"End: Not has seller invalid seller option", automation));
						logger.info("Task " + taskNumber + " "+ i + "/" + n + ": asin {} on {} s", asin, (System.currentTimeMillis() / 1000) - beginTime);
						continue;
					}
					long sellerPrice = sellerData.getPrice();
					long couponPrice = productData.getCouponPrice(sellerPrice);
					long point = productData.getPoint();
					automationLogService.save(
							new AutomationLog(AutomationLogType.NORMAL, "Seller: " + sellerData.getSellerName() + " | Price: " + sellerPrice, automation));
					long checkPrice = sellerPrice;
					if(automation.getPriceOption() != null) {
						if (automation.getPriceOption().equals(PriceOption.MINUS_POINT)) {
							checkPrice = checkPrice - point;
						}
						if (automation.getPriceOption().equals(PriceOption.MINUS_COUPON)) {
							checkPrice = checkPrice - couponPrice;
						}
					}
					if (checkPrice > automation.getPrice()) {
						automationLogService.save(new AutomationLog(AutomationLogType.END,
								"End: Not has seller invalid buy price", automation));
						logger.info("Task " + taskNumber + " "+ i + "/" + n + ": asin {} on {} s", asin, (System.currentTimeMillis() / 1000) - beginTime);
						continue;
					}
	
					automation = automationService.findById(automation.getId()).get(); //reload purchaseQuantity
					int purchaseQuantity = automation.getPurchaseQuantity() == null ? 0 : automation.getPurchaseQuantity(); //Total new product quantity will order
					int reQuantity = automation.getQuantity() - purchaseQuantity; //Remain product quantity allow order
					if(reQuantity <= 0) {
						automationLogService.save(new AutomationLog(AutomationLogType.END,
								"End: bought out of quantity", automation));
						logger.info("Task " + taskNumber + " "+ i + "/" + n + ": asin {} on {} s", asin, (System.currentTimeMillis() / 1000) - beginTime);
						continue;
					}
					
					int accountSize = 1;
					if(automation.getAccounts() != null) {
						accountSize = automation.getAccounts().split(",").length;
					}
					int maxQuantity = productData.getMaxQuantity();
					int buyQuantity = reQuantity;
					QuantityOption quantityOption = automation.getQuantityOption();
					Integer quantityOptionValue = automation.getQuantityOptionValue();
					if (quantityOption == null || quantityOption.equals(QuantityOption.MAX_QUANTITY)) {
						maxQuantity = (quantityOptionValue == null || maxQuantity < quantityOptionValue) ? maxQuantity
								: quantityOptionValue;
					} else if (quantityOption.equals(QuantityOption.DIVIDE_EQUALLY)) {
						buyQuantity = reQuantity / accountSize;
					}
					buyQuantity = (buyQuantity < maxQuantity) ? buyQuantity : maxQuantity;

					if (buyQuantity > 0) {
						OrderData orderData = amazonService.buyProduct(driver, account, buyQuantity,
								sellerData.getIndex());
						if (orderData.isSuccess()) {
							// Save order
							Seller seller = sellerService.findByName(sellerData.getSellerName());
							if(seller == null) {
					    		seller = sellerService.save(new Seller(sellerData.getSellerName()));
					    	}
							Order order = new Order();
							order.setUserId(automation.getCreatedBy());
							order.setAutomationId(automation.getId());
							order.setProduct(product);
							order.setAmazonAccountId(account.getId());
							order.setAmazonAccountEmail(account.getEmail());
							order.setSellerId(seller.getId());
							order.setSellerName(seller.getName());
							order.setQuantity(buyQuantity);
							order.setPrice(sellerPrice);
							order.setPoint(point);
							order.setCoupon(couponPrice);
							orderService.save(order);
							// Log
							automationLogService.save(new AutomationLog(AutomationLogType.ORDER,
									"Order [Seller: " + sellerData.getSellerName() + ", quantity: " + buyQuantity
											+ ", price: " + sellerPrice + ", point: " + point + ", coupon: "
											+ couponPrice + "]",
									automation));
							
                            //Save purchase quantity for automation
							automation.setPurchaseQuantity(buyQuantity + purchaseQuantity);
							automationService.save(automation);
							
							// Send line notify
							String lineMessage = "Name: " + product.getName() + "\n" + "ASIN: " + product.getAsin() + "\n" + "URL: "
									+ product.getImage() + "\n" + "Price: " + sellerPrice + "\n" + "Buy quantity: "
									+ buyQuantity + "\n" + "Account: \n" + account.getEmail();
							try {
								lineNotifyService.sendNotify(lineMessage);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							automationLogService.save(new AutomationLog(AutomationLogType.ORDER,
									"Order error: " + orderData.getMessage(), automation));
						}
					}
				} else {
					error ++;
					if(crawlProductData.getStatus().equals("CAPTCHA")) {
						logger.error("Error: Account {} has captcha", account.getEmail());
						break;
					}
				}

				automationLogService.save(new AutomationLog(AutomationLogType.END, "End", automation));
		
				logger.info("Task " + taskNumber + " "+ i + "/" + n + ": asin {} on {} s", asin, (System.currentTimeMillis() / 1000) - beginTime);
			}	
		}
		catch (Exception e) {
			e.printStackTrace();			
		}
		finally {			
			if(driver != null) driver.quit();
		}
		return CompletableFuture.completedFuture(new AutomationAsyncTaskResult(success, error));
	}
}
