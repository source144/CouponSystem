package Bean;

import java.util.ArrayList;

/**
 * An Object that represents a Customer
 * 
 * @author Gonen Matias
 * @version 1.0 30/01/2018
 */
public class Customer {

	private String name, password;
	private final long id;
	private ArrayList<Coupon> coupons;

	/**
	 * Constructs a new Customer
	 * 
	 * @param name
	 *            the name of this Customer
	 * @param password
	 *            the password of this Customer
	 * @param id
	 *            the id that represents this Customer
	 */
	public Customer(String name, String password, long id) {
		this.name = name;
		this.password = password;
		this.id = id;
		coupons = new ArrayList<>();
	}

	/**
	 * Gets the name of this Company
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of this Company
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the password of this Company
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password of this Company
	 * 
	 * @param password
	 *            the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the Coupon(s) this Company owns
	 * 
	 * @return the Coupon(s)
	 */
	public ArrayList<Coupon> getCoupons() {
		return coupons;
	}

	/**
	 * Sets the Coupon(s) this Company owns
	 * 
	 * @param coupons
	 *            the new Coupon(s)
	 */
	public void setCoupons(ArrayList<Coupon> coupons) {
		this.coupons = coupons;
	}

	/**
	 * Gets the ID of this Company
	 * 
	 * @return the ID
	 */
	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Customer [ID=" + this.id + "] " + this.name + ", has " + this.coupons.size() + " coupons";
	}

}
