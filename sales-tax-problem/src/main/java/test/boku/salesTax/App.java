package test.boku.salesTax;

import java.util.List;

import test.boku.salesTax.entity.Receipt;
import test.boku.salesTax.service.TaxService;

/**
 * @author hdk-pnchl
 *
 */
public class App {
	public static void main(String[] args){
		TaxService taxServiceInstance = TaxService.getInstance();
		List<Receipt> receiptList = taxServiceInstance.processOrder("/input.txt");
		taxServiceInstance.printReceipt(receiptList);
	}
}
