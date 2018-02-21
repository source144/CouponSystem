package Exceptions.NotFound;

import Exceptions.ItemType;
import Exceptions.MessageType;

/**
 * A Coupon Not Found ItemException in the Coupon System
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 */
public final class CouponNotFound extends NotFound {

	/**
	 * Constructs a new Coupon NotFound ItemException with a specified error
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
	public CouponNotFound(MessageType type, String title, long id, String details) {
		super(type, ItemType.COUPON, title, id, details);
	}

	/**
	 * Constructs a new Coupon NotFound ItemException
	 * 
	 * @param type
	 *            the type of exception
	 * @param title
	 *            the title of the Coupon
	 * @param id
	 *            the ID of the Coupon
	 */
	public CouponNotFound(MessageType type, String title, long id) {
		super(type, ItemType.COUPON, title, id);
	}
}
