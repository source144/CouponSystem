package Exceptions.AlreadyExists;

import Exceptions.ItemType;
import Exceptions.MessageType;

/**
 * A Company Already Exists ItemException in the Coupon System
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 */
public final class CompanyAlreadyExists extends AlreadyExists {

	/**
	 * Constructs a new Company AlreadyExists ItemException with a specified error
	 * description
	 * 
	 * @param type
	 *            the type of exception
	 * @param name
	 *            the name of the Company
	 * @param id
	 *            the ID of the Company
	 * @param details
	 *            detailed error message
	 */
	public CompanyAlreadyExists(MessageType type, String name, long id, String details) {
		super(type, ItemType.COMPANY, name, id, details);
	}

	/**
	 * Constructs a new Company AlreadyExists ItemException
	 * 
	 * @param type
	 *            the type of exception
	 * @param name
	 *            the name of the Company
	 * @param id
	 *            the ID of the Company
	 */
	public CompanyAlreadyExists(MessageType type, String name, long id) {
		super(type, ItemType.COMPANY, name, id);
	}
}
