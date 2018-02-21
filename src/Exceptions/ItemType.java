package Exceptions;

/**
 * An ENUM of all Item Types
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 */
public enum ItemType {
	COUPON("Coupon", "Coupons"), CUSTOMER("Customer", "Customers"), COMPANY("Company", "Companies");

	final String stringValue, pluralValue;

	private ItemType(String stringValue, String pluralValue) {
		this.stringValue = stringValue;
		this.pluralValue = pluralValue;
	}

	/**
	 * Gets the String value of this ItemType
	 * 
	 * @return the String value
	 */
	@Override
	public String toString() {
		return this.stringValue;
	}

	/**
	 * Gets the lower case String value of this ItemType
	 * 
	 * @return the lower case String value
	 */
	public String Lower() {
		return this.stringValue.toLowerCase();
	}

	/**
	 * Gets the upper case String value of this ItemType
	 * 
	 * @return the upper case String value
	 */
	public String Upper() {
		return this.stringValue.toUpperCase();
	}

	/**
	 * Gets the plural String value of this ItemType
	 * 
	 * @return the plural String value
	 */
	public String plural() {
		return this.pluralValue;
	}

	/**
	 * Gets the lower case plural String value of this ItemType
	 * 
	 * @return the lower case plural String value
	 */
	public String pluralLower() {
		return this.pluralValue.toLowerCase();
	}

	/**
	 * Gets the upper case plural String value of this ItemType
	 * 
	 * @return the upper case plural String value
	 */
	public String pluralUpper() {
		return this.pluralValue.toUpperCase();
	}
}
