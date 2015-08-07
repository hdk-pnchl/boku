package test.boku.salesTax;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.boku.salesTax.common.AppConstants;
import test.boku.salesTax.entity.Product;
import test.boku.salesTax.entity.Receipt;
import test.boku.salesTax.service.TaxService;
import test.boku.salesTax.util.TaxUtil;

/**
 * @author hdk-pnchl
 * 
 * Unit test for BOKU Salextax App.
 */
public class AppTest {
	@BeforeClass
	public static void beforeFirstTest() throws Exception{}
	
	@AfterClass
	public static void afterLastTest() throws Exception{}
	
	@Before
	public void beforeEachTest() throws Exception{}
	
	@After
	public void afterEachTest() throws Exception{}
	
	/**
	 * This test is only make sure, the end-to-end flow is working without any exception.
	 */
	@Test
	public void testOrder(){
		TaxService taxServiceInstance = TaxService.getInstance();
		List<Receipt> receiptList = taxServiceInstance.processOrder("/input.txt");
		taxServiceInstance.printReceipt(receiptList);
		assertTrue(true);
	}
	
	@Test
	public void testReceipt(){
		TaxService taxServiceInstance = TaxService.getInstance();
		
		List<String> productDetailList = new ArrayList<String>();
		productDetailList.add("1 imported bottle of perfume at 27.99");
		productDetailList.add("1 bottle of perfume at 18.99");
		productDetailList.add("1 packet of headache pills at 9.75");
		productDetailList.add("1 imported box of chocolates at 11.25");
		
		Receipt receipt = taxServiceInstance.processReceiptInternal(productDetailList);
		float totalTax = Float.valueOf(AppConstants.DECIMAL_FORMAT.format(receipt.calculateTotalTax()));
		float total = Float.valueOf(AppConstants.DECIMAL_FORMAT.format(receipt.calculateTotal()));
		assertTrue(totalTax == 6.70f);
		assertTrue(total == 74.68f);
	}
	
	@Test
	public void testProduct(){
		TaxService taxServiceInstance = TaxService.getInstance();
		
		String productDetailStr = "1 imported box of chocolates at 11.25";
		Product product = taxServiceInstance.processProduct(productDetailStr);
		
		assertTrue(product.getQuantity() == 1);
		assertTrue(product.getProductDesc().equals("imported box of chocolates"));
		
		float totalPrice = Float.valueOf(AppConstants.DECIMAL_FORMAT.format(product.calculateTotalPrice()));
		assertTrue(totalPrice == 11.85f);
		
		assertTrue(product.getPrice() == 11.25f);
		
		float totalTax = Float.valueOf(AppConstants.DECIMAL_FORMAT.format(product.calculateTotalTaxWithDiff()));
		assertTrue(totalTax == 0.60f);
	}
	
	@Test
	public void testProductDetailFiltering(){
		TaxService taxServiceInstance = TaxService.getInstance();
		
		String filteredProductDetail = "imported box of chocolates at";
		assertTrue(taxServiceInstance.filterProductDetail(filteredProductDetail).equals("imported box of chocolates"));
	}
	
	@Test
	public void testProcessTaxRounding(){
		float f = TaxUtil.roundToDecimal(22.342f, 2);
		assertTrue(f == 22.35f);
	}
	
	@Test
	public void testRoundToDecimal(){
		float f = TaxUtil.processTaxRounding(22.32f);
		assertTrue(f == 22.35f);
	}
}