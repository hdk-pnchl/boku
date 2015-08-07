package test.boku.salesTax.entity;

import java.util.ArrayList;
import java.util.List;

import test.boku.salesTax.common.AppConstants;

/**
 * @author hdk-pnchl
 *
 * Along with collection of Products, does also encapsulates total tax and total price calculation.
 */
public class Receipt {
	private List<Product>	productList	= new ArrayList<Product>();
	
	/* ------------------ SETTERS|GETTERS ------------------ */
	public List<Product> getProductList(){
		return productList;
	}
	
	public void setProductList(List<Product> productList){
		this.productList = productList;
	}
	
	/* ------------------ BAHAVIOUR ------------------ */
	
	public float calculateTotalTax(){
		float totalTax = 0;
		for(Product product: this.getProductList()){
			float tax = product.calculateTotalTaxWithDiff();
			totalTax = totalTax + tax;
		}
		return totalTax;
	}
	
	public float calculateTotal(){
		float total = 0;
		for(Product product: this.getProductList()){
			total = total + product.calculateTotalPrice();
		}
		return total;
	}
	
	public float calculateTotalWithoutTax(){
		float total = 0;
		for(Product product: this.getProductList()){
			float price = product.getPrice();
			total = total + price;
		}
		return total;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(Product product: this.getProductList()){
			sb.append(product.toString());
			sb.append("\n");
		}
		sb.append("Sales Taxes:").append(AppConstants.DECIMAL_FORMAT.format(this.calculateTotalTax()));
		sb.append("\n");
		sb.append("Total:").append(AppConstants.DECIMAL_FORMAT.format(this.calculateTotal()));
		return sb.toString();
	}
}