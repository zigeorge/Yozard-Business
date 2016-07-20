package com.yozard.business.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Calculations {

	public static String formatString_ToString(String formatThisString) {
		String formattedString = "";
		if (formatThisString != null) {
			if (formatThisString.equals("0.0")) {
				formattedString = "0";
				System.out.println("Enter sandman");
			} else {
				try {
					double num=Double.parseDouble(formatThisString);
					int numInt=(int) num;
					double tempLast=(double)numInt;
					
					System.out.println("originalM::"+formatThisString+" newM:"+tempLast);
					
				if(tempLast==num){
					formattedString=String.valueOf(formatThisString);
				}else{
					
				NumberFormat df = DecimalFormat.getInstance();
				df.setMinimumFractionDigits(0);
				df.setMaximumFractionDigits(2);
				df.setRoundingMode(RoundingMode.DOWN);
				double whole_num=Double.parseDouble(formatThisString);
				
				/*formattedString = new DecimalFormat("#0.00").format(Double
							.parseDouble(formatThisString));*/
				formattedString=df.format(whole_num);
				
				System.out.println("formattedString:: "+formatThisString);
				}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		return formattedString;
	}
	
}
