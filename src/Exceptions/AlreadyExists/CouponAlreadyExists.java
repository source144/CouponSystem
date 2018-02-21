package Exceptions.AlreadyExists;

import Exceptions.ItemType;
import Exceptions.MessageType;

/**
 * A Coupon Already Exists ItemException in the Coupon System
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 */
public final class CouponAlreadyExists extends AlreadyExists {

	/**
	 * Constructs a new Coupon AlreadyExists ItemException with a specified error
	 * description
	 * 
	 * @param type
	 *            the type of exception
	 * @param title
	 *            the title of the Coupon
	 * @param id
	 *            the ID of the Coupon
	 * @param details
	 *            detailed error message
	 */
	public CouponAlreadyExists(MessageType type, String title, long id, String details) {
		super(type, ItemType.COUPON, title, id, details);
	}

	/**
	 * Constructs a new Coupon AlreadyExists ItemException
	 * 
	 * @param type
	 *            the type of exception
	 * @param title
	 *            the title of the Coupon
	 * @param id
	 *            the ID of the Coupon
	 */
	public CouponAlreadyExists(MessageType type, String title, long id) {
		super(type, ItemType.COUPON, title, id);
	}
}
