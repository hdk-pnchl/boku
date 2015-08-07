package test.boku.salesTax.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import test.boku.salesTax.common.AppConstants;
import test.boku.salesTax.entity.Product;
import test.boku.salesTax.entity.Receipt;

/**
 * @author hdk-pnchl
 *
 */
public class TaxService {
	private static volatile TaxService	taxServiceInstance	= null;
	private HashMap<String, Pattern>	regexMap			= null;
	
	private TaxService(){
		this.init();
	}
	
	public static TaxService getInstance(){
		if(TaxService.taxServiceInstance == null){
			synchronized(TaxService.class){
				if(TaxService.taxServiceInstance == null){
					TaxService.taxServiceInstance = new TaxService();
				}
			}
		}
		return TaxService.taxServiceInstance;
	}
	
	private void init(){
		Properties configProp = new Properties();
		InputStream ipStream = null;
		try{
			ipStream = getClass().getResourceAsStream("/config.properties");
			if(ipStream != null){
				configProp.load(ipStream);
				
				regexMap = new HashMap<String, Pattern>();
				
				Pattern pattern = Pattern.compile(configProp.getProperty(AppConstants.PRODUCTS_REGEX_FOOD), Pattern.CASE_INSENSITIVE);
				regexMap.put(AppConstants.PRODUCTS_REGEX_FOOD, pattern);
				
				pattern = Pattern.compile(configProp.getProperty(AppConstants.PRODUCTS_REGEX_MEDICAL), Pattern.CASE_INSENSITIVE);
				regexMap.put(AppConstants.PRODUCTS_REGEX_MEDICAL, pattern);
				
				pattern = Pattern.compile(configProp.getProperty(AppConstants.PRODUCTS_REGEX_BOOK), Pattern.CASE_INSENSITIVE);
				regexMap.put(AppConstants.PRODUCTS_REGEX_BOOK, pattern);
				
				pattern = Pattern.compile(configProp.getProperty(AppConstants.PRODUCTS_REGEX_IMPORTED), Pattern.CASE_INSENSITIVE);
				regexMap.put(AppConstants.PRODUCTS_REGEX_IMPORTED, pattern);
				
			}else{
				System.err.println("config.properties not found");
			}
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		finally{
			if(ipStream != null){
				try{
					ipStream.close();
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public Pattern getRegexPatternByKey(String key){
		return this.regexMap.get(key);
	}
	
	public List<Receipt> processOrder(String filePath){
		List<Receipt> receiptList = new ArrayList<Receipt>();
		if(filePath != null && filePath.isEmpty() == false){
			BufferedReader br = null;
			InputStream ipStream = null;
			try{
				ipStream = getClass().getResourceAsStream(filePath);
				if(ipStream != null){
					br = new BufferedReader(new InputStreamReader(ipStream));
					List<String> productDetailList = null;
					String productDetailStr = null;
					while((productDetailStr = br.readLine()) != null){
						if(productDetailStr != null && productDetailStr.isEmpty() == false){
							String[] productDetailTokenAry = productDetailStr.split(" ");
							if(productDetailTokenAry.length == 2 && productDetailTokenAry[0].equalsIgnoreCase(AppConstants.PREFIX_NEW_RECEIPT)){
								this.processReceipt(productDetailList, receiptList);
								productDetailList = new ArrayList<String>();
							}else{
								productDetailList.add(productDetailStr);
							}
						}
					}
					this.processReceipt(productDetailList, receiptList);
				}else{
					System.err.println("Input file couldnt found: " + filePath);
				}
			}
			catch(FileNotFoundException e){
				e.printStackTrace();
			}
			catch(IOException e){
				e.printStackTrace();
			}
			finally{
				try{
					if(br != null){
						br.close();
					}
				}
				catch(IOException ex){
					ex.printStackTrace();
					System.err.println("Issue while closing file: " + filePath);
				}
			}
		}else{
			System.err.println("File path is not present:" + filePath);
		}
		return receiptList;
	}
	
	public void processReceipt(List<String> productDetailList, List<Receipt> receiptList){
		if(productDetailList != null){
			Receipt receipt = this.processReceiptInternal(productDetailList);
			if(receipt != null){
				receiptList.add(receipt);
			}
		}
	}
	
	public Receipt processReceiptInternal(List<String> productDetailList){
		Receipt receipt = null;
		if(productDetailList != null && productDetailList.size() > 0){
			receipt = new Receipt();
			for(String productDetailStr: productDetailList){
				Product product = this.processProduct(productDetailStr);
				if(product != null){
					receipt.getProductList().add(product);
				}
			}
		}
		return receipt;
	}
	
	public Product processProduct(String productDetailStr){
		Product product = null;
		if(productDetailStr != null && productDetailStr.isEmpty() == false){
			productDetailStr = this.filterProductDetail(productDetailStr);
			String[] productDetailTokenAry = productDetailStr.split(" ");
			if(productDetailTokenAry != null && productDetailTokenAry.length > 2){
				product = new Product();
				StringBuilder productDesc = new StringBuilder();
				for(int i = 0; i < productDetailTokenAry.length; i++){
					String tokenStr = productDetailTokenAry[i];
					if(tokenStr != null){
						if(i == 0){
							int quantity = Integer.valueOf(tokenStr);
							product.setQuantity(quantity);
						}else if(i == productDetailTokenAry.length - 1){
							float price = Float.valueOf(tokenStr);
							product.setPrice(price);
						}else if(i == productDetailTokenAry.length - 2){
							productDesc.append(tokenStr);
							product.setProductDesc(productDesc.toString());
						}else{
							productDesc.append(tokenStr).append(" ");
						}
					}
				}
				product.processTax();
			}else{
				System.err.println("Product detail string is either empty or having insufficient data: strTokenAry must have atleast 3 tokens -quantity, productDesc, price");
			}
		}else{
			System.err.println("Product detail string is either empty or having insufficient data: strTokenAry must have atleast 3 tokens -quantity, productDesc, price");
		}
		return product;
	}
	
	public String filterProductDetail(String productDetail){
		String filteredProductDetail = null;
		if(productDetail != null){
			filteredProductDetail = productDetail.replaceAll(" at", "");
		}
		return filteredProductDetail;
	}
	
	public void printReceipt(List<Receipt> receiptList){
		for(int i = 0; i < receiptList.size(); i++){
			System.out.println("Output " + (i + 1) + ":");
			Receipt receipt = receiptList.get(i);
			System.out.println(receipt.toString());
			System.out.println("\n");
		}
	}
}