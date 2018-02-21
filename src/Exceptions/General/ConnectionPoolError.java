package Exceptions.General;

import Exceptions.CouponSystemException;
import Exceptions.MessageType;

/**
 * An exception that provides information on a ConnectionPool access error or
 * other errors.
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 */
public final class ConnectionPoolError extends CouponSystemException {

	/**
	 * Constructs a ConnectionPoolError Exception
	 * 
	 * @param type
	 *            the type of exception
	 * @param error
	 *            the message of the error
	 * @param details
	 *            the details of the error
	 */
	public ConnectionPoolError(MessageType type, String error, String details) {
		super(type, "[{Pool}] " + error, details);
	}
}
