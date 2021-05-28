package com.ursa.tools.amazon.constant;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class MoneyFormat {

	public static Long parse(String text) {
		try {
			String money = ditgits(text);
			DecimalFormat numberFormat = (DecimalFormat) NumberFormat.getInstance(new Locale("en", "EN"));		
			numberFormat.applyPattern("#,###.##");
			return numberFormat.parse(money).longValue();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String ditgits(String text) {
		String result = "";
		if (text != null && !"".equals(text.trim())) {
			for (int i = 0; i < text.length(); i++) {
				if (Character.isDigit(text.charAt(i))) {
					result += text.charAt(i);
				}
			}
		}
		return result;
	}
}
