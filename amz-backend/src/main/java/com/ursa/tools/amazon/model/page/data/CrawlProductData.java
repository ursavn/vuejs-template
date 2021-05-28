package com.ursa.tools.amazon.model.page.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CrawlProductData {
	private ProductData data;
	private String status;
}
