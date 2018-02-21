package Exceptions.NotFound;

import Exceptions.ItemType;
import Exceptions.MessageType;

/**
 * A Customer Not Found ItemException in the Coupon System
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 */
public final class CustomerNotFound extends NotFound {
	/**
	 * Constructs a new Customer NotFound ItemException with a specified error
	 * description
	 * 
	 * @param type
	 *            the type of exception
	 * @param name
	 *            the name of the Customer
	 * @param id
	 *            the ID of the Customer
	 * @param details
	 *            detailed error message
	 */
	public CustomerNotFound(MessageType type, String name, long id, String details) {
		super(type, ItemType.CUSTOMER, name, id, details);
	}

	/**
	 * Constructs a new Customer NotFound ItemException
	 * 
	 * @param type
	 *            the type of exception
	 * @param name
	 *            the name of the Customer
	 * @param id
	 *            the ID of the Customer
	 */
	public CustomerNotFound(MessageType type, String name, long id) {
		super(type, ItemType.CUSTOMER, name, id);
	}
}
