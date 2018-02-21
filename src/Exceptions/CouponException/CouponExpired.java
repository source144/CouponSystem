package Exceptions.CouponException;

import java.sql.Date;
import java.text.SimpleDateFormat;

import Exceptions.ItemException;
import Exceptions.ItemType;
import Exceptions.MessageType;
import Exceptions.PreparedMessage;

/**
 * A Coupon Expired ItemException in the Coupon system<br>
 * (StockEmpty is nicer than OutOfStock)
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 *
 */
public final class CouponExpired extends ItemException {

	private final Date expDate;

	/**
	 * Constructs a new CouponExpired ItemException with a specified error
	 * description
	 * 
	 * @param type
	 *            the Exception's message tag
	 * @param title
	 *            the Coupon's title
	 * @param id
	 *            the Coupon's ID
	 * @param expDate
	 *            the Coupon's expiration date
	 * @param details
	 *            the error description
	 */
	public CouponExpired(MessageType type, String title, long id, Date expDate, String details) {
		super(type, ItemType.COUPON, title, id, PreparedMessage.Expired, details);
		this.expDate = expDate;
	}

	/**
	 * Constructs a new CouponExpired ItemException
	 * 
	 * @param type
	 *            the Exception's message tag
	 * @param title
	 *            the Coupon's title
	 * @param id
	 *            the Coupon's ID
	 * @param expDate
	 *            the Coupon's expiration date
	 */
	public CouponExpired(MessageType type, String title, long id, Date expDate) {
		this(type, title, id, expDate, "Coupon '" + title + "' (ID=" + id + ") has expired since "
				+ (new SimpleDateFormat("dd/MM/yyy")).format(expDate) + ".");
	}

	/**
	 * Gets the Coupon's expiration date from this CouponExpired exception
	 * 
	 * @return the Coupon's expiration date
	 */
	public final Date getExpirationDate() {
		return this.expDate;
	}
}
