package com.ursa.tools.amazon.model.page;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ursa.tools.amazon.constant.MoneyFormat;
import com.ursa.tools.amazon.constant.WebPage;
import com.ursa.tools.amazon.constant.enums.ProductCouponDataType;
import com.ursa.tools.amazon.model.AmazonAccount;
import com.ursa.tools.amazon.model.page.data.OrderData;
import com.ursa.tools.amazon.model.page.data.ProductCouponData;
import com.ursa.tools.amazon.model.page.data.ProductData;
import com.ursa.tools.amazon.model.page.data.SellerData;
import com.ursa.tools.amazon.util.RandomNumber;

public class ProductPage {

	Logger logger = LoggerFactory.getLogger(getClass());

	private WebDriver driver;
	private String asin;
	private String url;
	private AmazonAccount account;

	@FindBy(xpath = "//span[@data-hook='rating-out-of-text']")
	private WebElement rate;

	@FindBy(id = "add-to-cart-button")
	private WebElement cartButton;

	@FindBy(id = "buy-now-button")
	private WebElement buyButton;

	public ProductPage(WebDriver driver, AmazonAccount account) {
		this.driver = driver;
		this.account = account;
		PageFactory.initElements(this.driver, this);
	}

	public void viewProduct(String asin) {
		this.asin = asin;
		this.url = WebPage.DOMAIN + WebPage.PRODUCT + "/" + asin;
		this.driver.get(this.url);
		if (this.driver.findElements(By.id("newAccordionRow")).size() > 0) {
			WebElement buyOneTime = this.driver.findElement(By.id("newAccordionRow"));
			buyOneTime.findElement(By.tagName("a")).click();
		}
	}
	
	public boolean hasCaptcha() {
		return this.driver.findElements(By.id("captchacharacters")).size() > 0;
	}

	public ProductData getProduct() {
		if (asin == null)
			return null;

		try {
			ProductData product = new ProductData();
			product.setAsin(this.asin);
			product.setUrl(this.url);
			product.setName(getTitle());
			product.setImage(getImage());
			product.setRate(getRate());
			product.setMaxQuantity(getMaxOptionQuantity());
			product.setPoint(getPoint());
			product.setCoupon(getCoupon());

			Long price = this.getDealPrice();
			if (price == null) {
				price = this.getPrice();
			}
			if(price == null) {
				price = this.getSalePrice();
			}

			// Get seller
			List<SellerData> sellers = new ArrayList<SellerData>();
			String mainSeller = getMainSeller();
			boolean hasMainSeller = false;
			if (mainSeller != null) {
				sellers.add(new SellerData(0, mainSeller, price, isPrime()));
				hasMainSeller = true;
			}

			if (this.driver.findElements(By.id("buybox-see-all-buying-choices")).size() > 0) {
				WebElement btnSeeAllBuy = this.driver.findElement(By.id("buybox-see-all-buying-choices"));
				btnSeeAllBuy.click();
				sellers.addAll(this.getOfferSellers(hasMainSeller));
			} else if (this.driver.findElements(By.className("olp-link-widget")).size() > 0) {
				WebElement btnSeeAllBuy = this.driver.findElement(By.className("olp-link-widget"))
						.findElement(By.tagName("a"));
				btnSeeAllBuy.click();
				sellers.addAll(this.getOfferSellers(hasMainSeller));
			} else {
				sellers.addAll(getOtherSellers());
			}

			product.setSellers(sellers);
			if (product.getSellers() != null && product.getSellers().size() > 0) {
				product.setPrice(product.getSellers().get(0).getPrice());
			}

			return product;
		} catch (Exception e) {
			logger.error("Crawl {} error {}", asin, e.getMessage());
			return null;
		}
	}

	public OrderData buy(int quantity, int sellerIndex) {
		try {			
			//Check coupon
			if(this.driver.findElements(By.id("couponBadgeRegularVpc")).size() > 0) {	
				if(this.driver.findElement(By.id("unclippedCoupon")).isDisplayed()) {			
					WebElement vpcButton = this.driver.findElement(By.id("vpcButton"));
					WebElement couponCheckbox = vpcButton.findElement(By.tagName("label"));
					couponCheckbox.click();
				}
			}
			this.driver.manage().timeouts().implicitlyWait(RandomNumber.getInt(2, 5), TimeUnit.SECONDS);
			//Add to cart
			WebElement btnAddToCart = null;
			if (this.driver.findElements(By.id("buybox-see-all-buying-choices")).size() > 0
					|| this.driver.findElements(By.className("olp-link-widget")).size() > 0) {
				if(sellerIndex == 0) {
					WebElement offer = this.driver.findElement(By.id("aod-pinned-offer"));
					btnAddToCart = offer.findElement(By.name("submit.addToCart"));
				} else {
					List<WebElement> offers = this.driver.findElements(By.xpath("//div[@id='aod-offer']"));
					WebElement offer = offers.get(sellerIndex - 1);
					btnAddToCart = offer.findElement(By.name("submit.addToCart"));					
				}				
			} else {
				if (sellerIndex == 0) {
					if (this.driver.findElements(By.id("add-to-cart-button")).size() > 0) {
						btnAddToCart = this.driver.findElement(By.id("add-to-cart-button"));
					} else if (this.driver.findElements(By.id("add-to-cart-button")).size() > 0) {
						btnAddToCart = this.driver.findElement(By.id("add-to-cart-button-ubb"));
					}
				} else {
					WebElement btnParent = this.driver.findElement(By.id("mbc-buybutton-addtocart-" + sellerIndex));
					btnAddToCart = btnParent.findElement(By.tagName("input"));
				}

				if (this.driver.findElements(By.id("quantity")).size() > 0) {
					Select slQuantity = new Select(this.driver.findElement(By.id("quantity")));
					slQuantity.selectByVisibleText(quantity + "");
				}
			}
			btnAddToCart.click();
			//Check out
			WebElement btnCheckout = new WebDriverWait(driver, 10)
					.until(ExpectedConditions.elementToBeClickable(By.id("hlb-ptc-btn-native")));
			this.driver.manage().timeouts().implicitlyWait(RandomNumber.getInt(2, 5), TimeUnit.SECONDS);
			btnCheckout.click();

			// Case: display password confirm
			try {
				WebElement btnSubmit = new WebDriverWait(driver, 1)
						.until(ExpectedConditions.elementToBeClickable(By.id("signInSubmit")));
				WebElement inputPassword = this.driver.findElement(By.id("ap_password"));
				inputPassword.sendKeys(account.getPassword());
				this.driver.manage().timeouts().implicitlyWait(RandomNumber.getInt(2, 5), TimeUnit.SECONDS);
				btnSubmit.click();

				// Check need verify email
				if (this.driver.findElements(By.id("resend-approval-link")).size() > 0) {
					return new OrderData(false, "Need verify account");
				}
			} catch (Exception e) {
			}
			
			//Case: confirm age
			this.driver.manage().timeouts().implicitlyWait(RandomNumber.getInt(1, 3), TimeUnit.SECONDS);
			if(this.driver.findElements(By.id("wineNoticeCheckbox")).size() > 0) {
				this.driver.findElement(By.id("wineNoticeCheckbox")).click();
			}

			// Case: display confirm order page
			try {
				WebElement btnPlaceOrder = new WebDriverWait(driver, 3)
						.until(ExpectedConditions.elementToBeClickable(By.id("placeYourOrder")));
				btnPlaceOrder.click();
				
				this.driver.manage().timeouts().implicitlyWait(RandomNumber.getInt(1, 3), TimeUnit.SECONDS);
				if(this.driver.findElements(By.name("forcePlaceOrder")).size() > 0) {
					WebElement btnForceOrder = this.driver.findElement(By.name("forcePlaceOrder"));
					btnForceOrder.click();
				}
				
				WebElement purchaseStatus = new WebDriverWait(driver, 2)
						.until(ExpectedConditions.elementToBeClickable(By.id("widget-purchaseConfirmationStatus")));
				if (purchaseStatus.findElements(By.className("a-color-success")).size() > 0) {
					return new OrderData(true, "");
				}
			} catch (Exception e) {
				logger.debug("confirm order not found");
			}

			// Case: display popup checkout
			try {
				new WebDriverWait(driver, 3)
						.until(ExpectedConditions.elementToBeClickable(By.id("turbo-checkout-iframe")));
				this.driver.switchTo().frame("turbo-checkout-iframe");
				WebElement btnTurboCheckout = this.driver.findElement(By.id("turbo-checkout-pyo-button"));
				btnTurboCheckout.click();

				return new OrderData(true, "");
			} catch (Exception e) {
				logger.debug("checkout button not found");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new OrderData(false, "error");
	}

	public int getMaxOptionQuantity() {
		if (this.driver.findElements(By.id("quantity")).size() > 0) {
			Select slQuantity = new Select(this.driver.findElement(By.id("quantity")));
			List<WebElement> options = slQuantity.getOptions();
			WebElement lastOption = options.get(options.size() - 1);
			String value = lastOption.getAttribute("value");
			if (value == null || "".equals(value)) {
				lastOption = options.get(options.size() - 2);
				value = lastOption.getAttribute("value");
			}
			return Integer.parseInt(value);
		}
		return 1;
	}

	private Long getPoint() {
		if (this.driver.findElements(By.id("price")).size() > 0) {
			WebElement pricePanel = this.driver.findElement(By.id("price"));
			if (pricePanel.findElements(By.cssSelector("span[class='a-color-price']")).size() > 0) {
				String pointText = pricePanel.findElement(By.cssSelector("span[class='a-color-price']")).getText();
				//Example: "68pt (1%)"
				return MoneyFormat.parse(pointText.split("pt")[0]);
			}
		}
		return 0L;
	}
	
	private ProductCouponData getCoupon() {
		Long coupon = 0L;
		ProductCouponDataType couponType = ProductCouponDataType.AMOUNT;
		if(this.driver.findElements(By.id("couponBadgeRegularVpc")).size() > 0) {	
			String couponText = "0";
			if(this.driver.findElement(By.id("unclippedCoupon")).isDisplayed()) {			
				WebElement vpcButton = this.driver.findElement(By.id("vpcButton"));
				couponText = vpcButton.findElement(By.className("a-text-bold")).getText();
			} else if(this.driver.findElement(By.id("clippedCoupon")).isDisplayed()) {
				WebElement clippedCoupon = this.driver.findElement(By.id("clippedCoupon"));
				couponText = clippedCoupon.findElement(By.className("a-text-bold")).getText();
			}
			coupon = MoneyFormat.parse(couponText);					
			if(couponText.contains("%")) {
				couponType = ProductCouponDataType.PERCENT;
			}
		}
		return new ProductCouponData(coupon, couponType);	
	}

	private boolean isPrime() {
		if(this.driver.findElements(By.id("ourprice_shippingmessage")).size() > 0) {
			WebElement shippingMessage = this.driver.findElement(By.id("ourprice_shippingmessage"));
			return shippingMessage.findElements(By.className("a-icon-prime")).size() > 0;
		}
		return false;
	}

	private String getTitle() {
		return this.driver.findElement(By.id("productTitle")).getText();
	}

	private Long getPrice() {
		if (this.driver.findElements(By.id("priceblock_ourprice")).size() > 0) {
			WebElement priceElement = this.driver.findElement(By.id("priceblock_ourprice"));
			return MoneyFormat.parse(priceElement.getText());
		}
		return null;
	}
	
	private Long getSalePrice() {
		if(this.driver.findElements(By.id("priceblock_saleprice")).size() > 0) {
			WebElement priceElement = this.driver.findElement(By.id("priceblock_saleprice"));
			return MoneyFormat.parse(priceElement.getText());
		}
		return null;
	}

	private Long getDealPrice() {
		if (this.driver.findElements(By.id("priceblock_dealprice")).size() > 0) {
			WebElement priceElement = this.driver.findElement(By.id("priceblock_dealprice"));
			return MoneyFormat.parse(priceElement.getText());
		}
		return null;
	}

	private Double getRate() {
		if (this.driver.findElements(By.xpath("//span[@data-hook='rating-out-of-text']")).size() > 0) {
			WebElement rateElement = this.driver.findElement(By.xpath("//span[@data-hook='rating-out-of-text']"));
			String starRate = rateElement.getText();
			String rate = starRate.replace(" out of 5", "").replace("星5つ中の", "");
			return Double.parseDouble(rate);
		}
		return null;
	}

	private String getImage() {
		WebElement image = this.driver.findElement(By.id("landingImage"));
		return image != null ? image.getAttribute("src") : null;
	}

	private String getMainSeller() {
		if (this.driver.findElements(By.id("tabular-buybox-truncate-1")).size() > 0) {
			WebElement soldBy = this.driver.findElement(By.id("tabular-buybox-truncate-1"));
			return soldBy != null ? soldBy.getText() : null;
		}
		return null;
	}

	private List<SellerData> getOtherSellers() {
		List<SellerData> sellers = new ArrayList<SellerData>();
		List<WebElement> listElement = this.driver.findElements(By.className("mbc-offer-row"));
		if (listElement.size() > 0) {
			int size = listElement.size();
			for (int i = 0; i < size; i++) {
				WebElement sellerElement = listElement.get(i);
				WebElement priceElement = sellerElement.findElement(By.id("mbc-price-" + (i + 1)));
				WebElement soldByElement = sellerElement.findElement(By.id("mbc-sold-by-" + (i + 1)));
				long price = MoneyFormat.parse(priceElement.getText());
				String sellerName = soldByElement.findElement(By.className("mbcMerchantName")).getText();

				SellerData item = new SellerData(i + 1, sellerName, price, false);
				sellers.add(item);
			}
		}
		return sellers;
	}

	public List<SellerData> getOfferSellers(boolean hasMainSeller) {
		List<SellerData> sellers = new ArrayList<SellerData>();
		WebDriverWait waitOffer = new WebDriverWait(driver, 10);
		waitOffer.until(ExpectedConditions.visibilityOfElementLocated(By.id("all-offers-display-scroller")));

		int sellerIndex = 0;
		
		//Main offer
		if(!hasMainSeller) {
			if(this.driver.findElements(By.id("aod-pinned-offer-show-more-link")).size() > 0) {
				this.driver.findElement(By.id("aod-pinned-offer-show-more-link")).click();
			}					
			WebElement mainOffer = this.driver.findElement(By.id("aod-pinned-offer"));
			if(mainOffer.findElements(By.id("aod-offer-soldBy")).size() > 0
					&& mainOffer.findElements(By.id("a-price-whole")).size() > 0) {
			   sellers.add(this.getOfferSeller(mainOffer, sellerIndex));
			}
		}
				
		//Other offer
		int offerCount = 0;
		List<WebElement> offers = this.driver.findElements(By.xpath("//div[@id='aod-offer']"));		
		while(offerCount < offers.size()) {
			offerCount = offers.size();
			
			((JavascriptExecutor) driver).executeScript ("arguments[0].scrollIntoView();", offers.get(offers.size() - 1));
			
			this.driver.manage().timeouts().implicitlyWait(RandomNumber.getInt(1, 3), TimeUnit.SECONDS);
			
			offers = this.driver.findElements(By.xpath("//div[@id='aod-offer']"));
		}
		for (WebElement offer : offers) {
			sellerIndex++;
			SellerData sellerData = this.getOfferSeller(offer, sellerIndex);
			sellers.add(sellerData);
		}
		return sellers;
	}
	
	private SellerData getOfferSeller(WebElement offer, int index) {
		long offerPrice = MoneyFormat.parse(offer.findElement(By.className("a-price-whole")).getText());
		WebElement soleBy = offer.findElement(By.id("aod-offer-soldBy"));
		String offerSoleBy = "Amazon.co.jp";
		if (soleBy.findElements(By.tagName("a")).size() > 0) {
			offerSoleBy = soleBy.findElement(By.tagName("a")).getText();
		}
		boolean isPrime = (offer.findElements(By.className("a-icon-prime")).size() > 0);
		return new SellerData(index, offerSoleBy, offerPrice, isPrime);		
	}
}
