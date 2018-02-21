package Exceptions.General;

import Exceptions.CouponSystemException;
import Exceptions.MessageType;


/**
 * A failed login attempt Exception
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 */
public final class BadUsernamePassword extends CouponSystemException { // Could provide information about who tried to log in.
	
	/**
	 * Constructs a BadUsernamePassword Exception
	 */
	public BadUsernamePassword() {
		super(MessageType.ERR, "Bad username or password.", "Either username does not exist or password is wrong.");
	}
}