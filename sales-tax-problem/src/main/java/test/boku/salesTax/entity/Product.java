package test.boku.salesTax.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import test.boku.salesTax.common.AppConstants;
import test.boku.salesTax.enumType.ProductType;
import test.boku.salesTax.enumType.TaxType;
import test.boku.salesTax.service.TaxService;
import test.boku.salesTax.util.TaxUtil;

/**
 * @author hdk-pnchl
 * 
 * Product hold detail about Product's within the Receipt.
 * Also encapsulates, sales and total price calculation logic.
 */
public class Product {
	public static final int		TAX_PERCENTAGE_SALES	= 10;
	public static final int		TAX_PERCENTAGE_IMPORT	= 5;
	
	private int					quantity				= 0;
	private ProductType			productType				= null;
	private String				productDesc				= null;
	private float				price					= 0;
	private boolean				isImported				= false;
	/**
	 * Tomorrow, there could be more taxes applied. With following, for each new tax we need not to update Product class each time.
	 */
	private Map<TaxType, Float>	taxMap					= new HashMap<TaxType, Float>();
	
	/* ------------------ CONSTRUCTOR ------------------ */
	
	public Product(){}
	
	/* ------------------ SETTERS|GETTERS ------------------ */
	
	public int getQuantity(){
		return quantity;
	}
	
	public void setQuantity(int quantity){
		this.quantity = quantity;
	}
	
	public ProductType getProductType(){
		return productType;
	}
	
	/**
	 * ProductType should only eb set from setProductDesc(productDesc), thus it private class.
	 * productDesc contains multiple feature about he product i.e. if taxable, which tax applied(sales, import)
	 * @param productType
	 */
	private void setProductType(ProductType productType){
		this.productType = productType;
	}
	
	public String getProductDesc(){
		return productDesc;
	}
	
	public void setProductDesc(String productDesc){
		this.productDesc = productDesc;
		this.setProductType(ProductType.matchProduct(productDesc));
		this.setImported(TaxService.getInstance().getRegexPatternByKey(AppConstants.PRODUCTS_REGEX_IMPORTED).matcher(productDesc).find());
	}
	
	public float getPrice(){
		return price;
	}
	
	public void setPrice(float price){
		price = TaxUtil.roundToDecimal(price, 2);
		this.price = price;
	}
	
	public boolean isImported(){
		return isImported;
	}
	
	public void setImported(boolean isImported){
		this.isImported = isImported;
	}
	
	/**
	 * This is private since its only used to keep applied tax and its type.
	 * processImportTax and processalesTax adds values to it and calculateTotalTax uses it to calculate total tax
	 * 
	 * @return
	 */
	private Map<TaxType, Float> getTaxMap(){
		return taxMap;
	}
	
	/* ------------------ BAHAVIOUR ------------------ */
	
	/**
	 * Encapsulating tax exemption logic, as it might vary.
	 * 
	 * @return
	 */
	public boolean isSalesTaxApplied(){
		boolean isSalesTaxApplied = false;
		if(this.getProductType().equals(ProductType.OTHERS)){
			isSalesTaxApplied = true;
		}
		return isSalesTaxApplied;
	}
	
	/**
	 * Would manage both, sales import tax. 
	 * 
	 * @return
	 */
	public boolean processTax(){
		boolean isTaxProcessed = false;
		/*
		 * if productDescription is present, productType and if imported is evaluated.
		 * */
		if(this.getPrice() != 0 && this.getProductDesc() != null){
			if(this.isSalesTaxApplied()){
				this.processSalesTax();
			}
			if(this.isImported()){
				this.processImportTax();
			}
			isTaxProcessed = true;
		}
		return isTaxProcessed;
	}
	
	private boolean processSalesTax(){
		boolean isSalesTaxProcessed = false;
		if(this.getPrice() != 0){
			this.getTaxMap().put(TaxType.TAX_SALLES, this.calculateSalesTax());
			isSalesTaxProcessed = true;
		}
		return isSalesTaxProcessed;
	}
	
	public float calculateSalesTax(){
		float salesTax = 0;
		if(this.getPrice() != 0){
			salesTax = (Product.TAX_PERCENTAGE_SALES / 100f) * this.getPrice();
		}
		//salesTax = Util.round(salesTax, 2);
		return salesTax;
	}
	
	private boolean processImportTax(){
		boolean isImportTaxProcessed = false;
		if(this.getPrice() != 0){
			this.getTaxMap().put(TaxType.TAX_IMPORT, this.calculateImportTax());
			isImportTaxProcessed = true;
		}
		return isImportTaxProcessed;
	}
	
	public float calculateImportTax(){
		float importTax = 0;
		if(this.getPrice() != 0){
			importTax = (Product.TAX_PERCENTAGE_IMPORT / 100f) * this.getPrice();
		}
		importTax = TaxUtil.roundToDecimal(importTax, 2);
		return importTax;
	}
	
	public float calculateTotalPrice(){
		float totalPrice = this.getPrice() + this.calculateTotalTax();
		totalPrice = TaxUtil.roundToDecimal(totalPrice, 2);
		return totalPrice;
	}
	
	public float calculateTotalTax(){
		float totalTax = 0;
		for(Entry<TaxType, Float> taxEntry: this.getTaxMap().entrySet()){
			if(taxEntry.getValue() != null){
				totalTax = totalTax + taxEntry.getValue();
			}
		}
		totalTax = TaxUtil.roundToDecimal(totalTax, 2);
		return totalTax;
	}
	
	/**
	 * There happens rounding after on totalTax calculation(i.e. importTax + salesTax),
	 * calculateSalesTax() !=  ( calculateTotalPrice() - calculateTotalTax())
	 * Since, final value is taken after rounding off, following method could be used to find actual tax paid.
	 * 
	 * @return
	 */
	public float calculateTotalTaxWithDiff(){
		float salesTax = this.calculateTotalPrice() - this.getPrice();
		return salesTax;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(this.getQuantity()).append(" ");
		sb.append(this.getProductDesc()).append(" ");
		sb.append(":").append(" ");
		sb.append(AppConstants.DECIMAL_FORMAT.format(this.calculateTotalPrice()));
		return sb.toString();
	}
}