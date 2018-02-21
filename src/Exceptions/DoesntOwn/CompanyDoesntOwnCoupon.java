package Exceptions.DoesntOwn;

import Exceptions.ItemType;
import Exceptions.JoinCouponException;
import Exceptions.MessageType;
import Exceptions.PreparedMessage;

/**
 * A Company Doesn't Own Coupon Exception in the Coupon system
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 *
 */
public final class CompanyDoesntOwnCoupon extends JoinCouponException {

	/**
	 * Constructs a new Company AlreadyOwnsCoupon Exception with a specified error
	 * description
	 * 
	 * @param type
	 *            the Exception's MessageType
	 * @param companyName
	 *            the Company's name
	 * @param companyId
	 *            the Company's ID
	 * @param couponTitle
	 *            the Coupon's title
	 * @param couponId
	 *            the Coupon's ID
	 * @param details
	 *            detailed error message
	 */
	public CompanyDoesntOwnCoupon(MessageType type, String companyName, long companyId, String couponTitle,
			long couponId, String details) {
		super(type, ItemType.COMPANY, companyName, companyId, couponTitle, couponId, PreparedMessage.NotOwn, details);
	}

	/**
	 * Constructs a new Company AlreadyOwnsCoupon Exception
	 * 
	 * @param type
	 *            the Exception's MessageType
	 * @param companyName
	 *            the Company's name
	 * @param companyId
	 *            the Company's ID
	 * @param couponTitle
	 *            the Coupon's title
	 * @param couponId
	 *            the Coupon's ID
	 */
	public CompanyDoesntOwnCoupon(MessageType type, String companyName, long companyId, String couponTitle,
			long couponId) {
		super(type, ItemType.COMPANY, companyName, companyId, couponTitle, couponId, PreparedMessage.NotOwn);
	}
}
