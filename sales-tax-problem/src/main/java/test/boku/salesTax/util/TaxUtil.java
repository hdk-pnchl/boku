package test.boku.salesTax.util;

import java.math.BigDecimal;

import test.boku.salesTax.common.AppConstants;

/**
 * @author hdk-pnchl
 *
 */
public class TaxUtil {
	public static float processTaxRounding(Float f){
		if(f != null && f != 0){
			String[] fAry = AppConstants.DECIMAL_FORMAT.format(f).split("");
			int j = Integer.valueOf(fAry[fAry.length - 1]);
			if(j != 0 && j != 5 && j <= 5){
				fAry[fAry.length - 1] = "5";
				StringBuilder sb = new StringBuilder();
				for(int i = 0; i < fAry.length; i++){
					sb.append(fAry[i]);
				}
				f = Float.valueOf(sb.toString());
			}
		}
		return f;
	}
	
	/**
	 * Round to certain number of decimals
	 * 
	 * @param d
	 * @param decimalPlace
	 * @return
	 */
	public static float roundToDecimal(Float d, int decimalPlace){
		float f = 0;
		if(d != null && d != 0){
			BigDecimal bd = new BigDecimal(d);
			bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_EVEN);
			f = bd.floatValue();
		}
		f = TaxUtil.processTaxRounding(new Float(f));
		return f;
	}
}