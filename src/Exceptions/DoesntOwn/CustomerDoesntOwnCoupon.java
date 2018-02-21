package Exceptions.DoesntOwn;

import Exceptions.ItemType;
import Exceptions.JoinCouponException;
import Exceptions.MessageType;
import Exceptions.PreparedMessage;

/**
 * A Customer Doesn't Own Coupon Exception in the Coupon system
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 *
 */
public final class CustomerDoesntOwnCoupon extends JoinCouponException {
	/**
	 * Constructs a new CustomerDoesntOwnCoupon Exception with a specified error
	 * description
	 * 
	 * @param type
	 *            the Exception's MessageType
	 * @param customerName
	 *            the Customer's name
	 * @param customerId
	 *            the Customer's ID
	 * @param couponTitle
	 *            the Coupon's title
	 * @param couponId
	 *            the Coupon's ID
	 * @param details
	 *            detailed error message
	 */
	public CustomerDoesntOwnCoupon(MessageType type, String customerName, long customerId, String couponTitle,
			long couponId, String details) {
		super(type, ItemType.CUSTOMER, customerName, customerId, couponTitle, couponId, PreparedMessage.NotOwn, details);
	}

	/**
	 * Constructs a new CustomerDoesntOwnCoupon Exception
	 * 
	 * @param type
	 *            the Exception's MessageType
	 * @param customerName
	 *            the Customer's name
	 * @param customerId
	 *            the Customer's ID
	 * @param couponTitle
	 *            the Coupon's title
	 * @param couponId
	 *            the Coupon's ID
	 */
	public CustomerDoesntOwnCoupon(MessageType type, String customerName, long customerId, String couponTitle,
			long couponId) {
		super(type, ItemType.CUSTOMER, customerName, customerId, couponTitle, couponId, PreparedMessage.NotOwn);
	}
}
