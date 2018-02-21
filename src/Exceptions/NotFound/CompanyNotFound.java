package Exceptions.NotFound;

import Exceptions.ItemType;
import Exceptions.MessageType;
/**
 * A Company Not Found ItemException in the Coupon System
 * 
 * @author Gonen Matias
 * @version 1.0 02/01/2018
 */
public final class CompanyNotFound extends NotFound {

	/**
	 * Constructs a new Company NotFound ItemException with a specified error
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
	public CompanyNotFound(MessageType type, String name, long id, String details) {
		super(type, ItemType.COMPANY, name, id, details);
	}

	/**
	 * Constructs a new Company NotFound ItemException
	 * 
	 * @param type
	 *            the type of exception
	 * @param name
	 *            the name of the Company
	 * @param id
	 *            the ID of the Company
	 */
	public CompanyNotFound(MessageType type, String name, long id) {
		super(type, ItemType.COMPANY, name, id);
	}
}
