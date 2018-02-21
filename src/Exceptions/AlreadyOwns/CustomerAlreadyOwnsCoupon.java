package Exceptions.AlreadyOwns;

import Exceptions.ItemType;
import Exceptions.JoinCouponException;
import Exceptions.MessageType;
import Exceptions.PreparedMessage;

/**
 * An Customer Already Owns Coupon Exception in the Coupon system
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 *
 */
public final class CustomerAlreadyOwnsCoupon extends JoinCouponException {
	/**
	 * Constructs a new Customer AlreadyOwnsCoupon Exception with a specified error
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
	public CustomerAlreadyOwnsCoupon(MessageType type, String customerName, long customerId, String couponTitle,
			long couponId, String details) {
		super(type, ItemType.CUSTOMER, customerName, customerId, couponTitle, couponId, PreparedMessage.Owns, details);
	}

	/**
	 * Constructs a new Customer AlreadyOwnsCoupon Exception
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
	public CustomerAlreadyOwnsCoupon(MessageType type, String customerName, long customerId, String couponTitle,
			long couponId) {
		super(type, ItemType.CUSTOMER, customerName, customerId, couponTitle, couponId, PreparedMessage.Owns);
	}
}
