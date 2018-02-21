package Exceptions.CouponException;

import Exceptions.ItemException;
import Exceptions.ItemType;
import Exceptions.MessageType;
import Exceptions.PreparedMessage;

/**
 * A Coupon Stock Empty ItemException in the Coupon system<br>
 * (StockEmpty is nicer than OutOfStock)
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 *
 */
public final class CouponStockEmpty extends ItemException {

	/**
	 * Constructs a new CouponStockEmpty ItemException with a specified error
	 * description
	 * 
	 * @param type
	 *            the Exception's message tag
	 * @param title
	 *            the Coupon's title
	 * @param id
	 *            the Coupon's ID
	 * @param details
	 *            the error description
	 */
	public CouponStockEmpty(MessageType type, String title, long id, String details) {
		super(type, ItemType.COUPON, title, id, PreparedMessage.StockEmpty, details);
	}

	/**
	 * Constructs a new CouponStockEmpty ItemException
	 * 
	 * @param type
	 *            the Exception's message tag
	 * @param title
	 *            the Coupon's title
	 * @param id
	 *            the Coupon's ID
	 */
	public CouponStockEmpty(MessageType type, String title, long id) {
		super(type, ItemType.COUPON, title, id, PreparedMessage.StockEmpty);
	}
}
