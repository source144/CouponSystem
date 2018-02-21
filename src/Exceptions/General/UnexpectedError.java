package Exceptions.General;

import Exceptions.CouponSystemException;
import Exceptions.MessageType;

/**
 * An Unexcpected error exception in the Coupon system
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 */
public final class UnexpectedError extends CouponSystemException {
	
	/**
	 * Constructs an UnexpectedError Exception
	 * 
	 * @param type
	 *            the type of exception
	 * @param error
	 *            the message of the error
	 * @param details
	 *            the details of the error
	 */
	public UnexpectedError(MessageType type, String error, String details) {
		super(type, error, details);
	}
}
