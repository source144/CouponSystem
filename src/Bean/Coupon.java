package Bean;

import java.sql.Date;

/**
 * An Object that represents a Coupon
 * 
 * @author Gonen Matias
 * @version 1.0 30/01/2018
 */
public class Coupon {
	private final long id;
	private String title, message, image;
	private Date startDate, endDate;
	private int amount;
	private CouponType type;
	private double price;

	/**
	 * Constructs a new Coupon
	 * 
	 * @param id
	 *            the id that represents this Coupon
	 * @param title
	 *            the title of this Coupon
	 * @param message
	 *            the message this Coupon contains
	 * @param image
	 *            the filepath of this Coupon's image
	 * @param startDate
	 *            this Coupon's start date
	 * @param endDate
	 *            this Coupon's expiration date
	 * @param amount
	 *            this Coupon's amount
	 * @param type
	 *            this Coupon's type
	 * @param price
	 *            this Coupon's price
	 */
	public Coupon(long id, String title, String message, String image, Date startDate, Date endDate, int amount,
			CouponType type, double price) {
		this.id = id;
		this.title = title;
		this.message = message;
		this.image = image;
		this.startDate = startDate;
		this.endDate = endDate;
		this.amount = amount;
		this.type = type;
		this.price = price;
	}

	/**
	 * Gets the title of this Coupon
	 * 
	 * @return the title of this Coupon
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of this Coupon
	 * 
	 * @param title
	 *            the new message
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the message of this Coupon
	 * 
	 * @return the message of this Coupon
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message of this Coupon
	 * 
	 * @param message
	 *            the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the image filepath of this Coupon
	 * 
	 * @return the image filepath
	 */
	public String getImage() {
		return image;
	}

	/**
	 * Sets the image filepath of this Coupon
	 * 
	 * @param image
	 *            the new filepath
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * Gets the start date of this Coupon
	 * 
	 * @return the start date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date of this Coupon
	 * 
	 * @param startDate
	 *            the new start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the expiration date of this Coupon
	 * 
	 * @return the expiration date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the expiration date of this Coupon
	 * 
	 * @param endDate
	 *            the new expiration date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets the amount of this Coupon
	 * 
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Sets the amount of this Coupon
	 * 
	 * @param amount
	 *            the new amount
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * Gets the CouponType of this Coupon
	 * 
	 * @return the CouponType
	 */
	public CouponType getType() {
		return type;
	}

	/**
	 * Sets the CouponType of this Coupon
	 * 
	 * @param type
	 *            the new CouponType
	 */
	public void setType(CouponType type) {
		this.type = type;
	}

	/**
	 * Gets the price of this Coupon
	 * 
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Sets the price of this Coupon
	 * 
	 * @param price
	 *            the new price
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * Gets the ID of this Coupon
	 * 
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return type.toString() + " Coupon '" + this.title + "' [ID=" + this.id + "] ," + this.amount
				+ " coupons left; Price: " + this.price + "$ Date: " + this.startDate + " - " + this.endDate;
	}

}
