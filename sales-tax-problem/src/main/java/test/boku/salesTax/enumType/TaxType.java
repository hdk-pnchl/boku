package test.boku.salesTax.enumType;

/**
 * @author hdk-pnchl
 * Bought product could be candidate of one or more type of tax. This class keeps all different type of taxes.
 */
public enum TaxType
{
	TAX_SALLES("SALLES"), TAX_IMPORT("IMPORT_TAX");
	
	private String	desc	= null;
	
	private TaxType(String desc){
		this.desc = desc;
	}
	
	public String getDesc(){
		return desc;
	}
}
