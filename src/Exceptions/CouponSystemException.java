package Exceptions;

/**
 * Represents an exception in the Coupon system
 * 
 * @author Gonen Matias
 * @version 1.0 30/01/2018
 */
public abstract class CouponSystemException extends Exception {
	private final String details;
	private final MessageType messageType;

	/**
	 * Constructs a new Coupon system Exception
	 * 
	 * @param type
	 *            the type of exception
	 * @param error
	 *            the message of the error
	 * @param details
	 *            the details of the error
	 */
	public CouponSystemException(MessageType type, String error, String details) {
		super(error);
		this.messageType = type;
		this.details = details;
	}

	/**
	 * Gets the MessageType of this CouponSystemException
	 * 
	 * @return the MessageType
	 */
	public final MessageType getExceptionType() {
		this.getMessage();
		return messageType;
	}

	/**
	 * Gets the MessageType tagged message of this CouponSystemException
	 * 
	 * @return the MessageType tagged message
	 */
	@Override
	public final String getMessage() {
		return (this.messageType.toString() + ": " + super.getMessage());
	}

	/**
	 * Gets the error message of this CouponSystemException
	 * 
	 * @return the error message
	 */
	public final String getError() {
		return super.getMessage();
	}

	/**
	 * Gets the detailed error message of this CouponSystemException
	 * 
	 * @return the detailed error message
	 */
	public final String getDetails() {
		return this.details;
	}
}
