package com.ursa.tools.amazon.payload;

import java.util.ArrayList;
import java.util.List;

import com.ursa.tools.amazon.model.Product;
import com.ursa.tools.amazon.model.ProductSeller;

import lombok.Data;

@Data
public class ProductResponse {
	private Integer id;
	private String asin;
    private String name;    
    private String image;    
    private Long price;    
    private Double rate;
	private List<ProductSellerResponse> sellers;
	
	public static ProductResponse mapper(Product product) {
		ProductResponse response = new ProductResponse();
		response.setId(product.getId());
    	response.setAsin(product.getAsin());
    	response.setName(product.getName());
    	response.setImage(product.getImage());
    	response.setPrice(product.getPrice());
    	response.setRate(product.getRate());
    	if(product.getProductSellers() != null) {
    		List<ProductSellerResponse> sellerResponses = new ArrayList<ProductSellerResponse>();
    	    for(ProductSeller productSeller: product.getProductSellers()) {
    	    	ProductSellerResponse item = new ProductSellerResponse();
    	    	item.setSellerId(productSeller.getSeller().getId());
    	    	item.setSellerName(productSeller.getSeller().getName());
    	    	item.setPrice(productSeller.getPrice());
    	    	sellerResponses.add(item);
    	    }
    	    response.setSellers(sellerResponses);
    	}	
    	return response;
	}
}
