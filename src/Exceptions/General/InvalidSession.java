package Exceptions.General;

import Exceptions.CouponSystemException;
import Exceptions.MessageType;
import Facade.ClientType;

/**
 * An exception that provides information on a Session access error.
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 */
public final class InvalidSession extends CouponSystemException { // Could store data like action attempted, request
																	// sender etc..

	private final ClientType clientType;
	
	/**
	 * Constructs an InvalidSession Exception
	 * 
	 * @param type
	 *            the type of exception
	 * @param clientType the Session's ClientType
	 * @param details
	 *            the details of the error
	 */
	public InvalidSession(MessageType type, ClientType clientType, String details) {
		super(type, "Invalid " + clientType + " session.", details);
		this.clientType = clientType;
	}

	/**
	 * Constructs an InvalidSession Exception
	 * 
	 * @param type
	 *            the type of exception
	 * @param clientType the Session's ClientType
	 */
	public InvalidSession(MessageType type, ClientType clientType) {
		this(type, clientType, "Unable to perform action, invalid " + clientType + " session.");
	}
	
	/**
	 * Gets this Session's ClientType
	 * @return the ClientType
	 */
	public final ClientType getClientType() {
		return this.clientType;
	}
}
