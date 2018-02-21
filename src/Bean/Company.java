package Bean;

import java.util.ArrayList;

/**
 * An Object that represents a Company
 * 
 * @author Gonen Matias
 * @version 1.0 30/01/2018
 */
public class Company {
	private String name, password, email;
	private final long id;
	private ArrayList<Coupon> coupons;

	/**
	 * Constructs a new Company
	 * 
	 * @param name
	 *            the name of this Company
	 * @param password
	 *            the password of this Company
	 * @param email
	 *            the email address of this Company
	 * @param id
	 *            the id that represents this Company
	 */
	public Company(String name, String password, String email, long id) {
		this.name = name;
		this.password = password;
		this.email = email;
		this.id = id;
		this.coupons = new ArrayList<>();
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
	 * Gets the email address of this Company
	 * 
	 * @return the email address
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email address of this Company
	 * 
	 * @param email
	 *            the new email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the Coupons(s) this Company owns
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
		return "Company [ID=" + this.id + "] " + this.name + " " + this.email + ", owns " + this.coupons.size()
				+ " coupons";
	}
}
