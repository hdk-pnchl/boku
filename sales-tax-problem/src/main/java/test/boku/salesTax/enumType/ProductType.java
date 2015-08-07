package test.boku.salesTax.enumType;

import test.boku.salesTax.common.AppConstants;
import test.boku.salesTax.service.TaxService;

/**
 * @author hdk-pnchl
 *
 * Product could be of many types. Each newly added product type would be managed here.
 */
public enum ProductType
{
	BOOKS("Books"), FOOD("Food"), MEDICAL("Medical"), OTHERS("Others");
	
	private String	typeDesc	= null;
	
	private ProductType(String typeDesc){
		this.typeDesc = typeDesc;
	}
	
	public String getTypeDesc(){
		return typeDesc;
	}
	
	public static ProductType matchProduct(String productDesc){
		ProductType productType = null;
		if(productDesc != null){
			if(productDesc.isEmpty()){
				productType = ProductType.OTHERS;
			}else{
				if(TaxService.getInstance().getRegexPatternByKey(AppConstants.PRODUCTS_REGEX_BOOK).matcher(productDesc).find()){
					productType = ProductType.BOOKS;
				}else if(TaxService.getInstance().getRegexPatternByKey(AppConstants.PRODUCTS_REGEX_FOOD).matcher(productDesc).find()){
					productType = ProductType.FOOD;
				}else if(TaxService.getInstance().getRegexPatternByKey(AppConstants.PRODUCTS_REGEX_MEDICAL).matcher(productDesc).find()){
					productType = ProductType.MEDICAL;
				}else{
					productType = ProductType.OTHERS;
				}
			}
		}
		return productType;
	}
}