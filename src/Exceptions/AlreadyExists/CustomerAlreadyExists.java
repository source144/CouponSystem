package Exceptions.AlreadyExists;

import Exceptions.ItemType;
import Exceptions.MessageType;

/**
 * A Company already exists ItemException in the Coupon System
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 */
public final class CustomerAlreadyExists extends AlreadyExists {

	/**
	 * Constructs a new Customer AlreadyExists ItemException with a specified error
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
	public CustomerAlreadyExists(MessageType type, String name, long id, String details) {
		super(type, ItemType.CUSTOMER, name, id, details);
	}

	/**
	 * Constructs a new Customer AlreadyExists ItemException
	 * 
	 * @param type
	 *            the type of exception
	 * @param name
	 *            the name of the Customer
	 * @param id
	 *            the ID of the Customer
	 */
	public CustomerAlreadyExists(MessageType type, String name, long id) {
		super(type, ItemType.CUSTOMER, name, id);
	}
}
