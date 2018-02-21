package Exceptions.NotFound;

import Exceptions.ItemException;
import Exceptions.ItemType;
import Exceptions.MessageType;
import Exceptions.PreparedMessage;

/**
 * A Not Found ItemException in the Coupon system
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 *
 */
public abstract class NotFound extends ItemException {

	/**
	 * Constructs a new NotFound ItemException with a specified error description
	 * 
	 * @param type
	 *            the Exception's message tag
	 * @param itemType
	 *            the item's type
	 * @param name
	 *            the item's name
	 * @param id
	 *            the item's ID
	 * @param details
	 *            the error description
	 */
	public NotFound(MessageType type, ItemType itemType, String name, long id, String details) {
		super(type, itemType, name, id, PreparedMessage.NotFound, details);
	}

	/**
	 * Constructs a new NotFound ItemException
	 * 
	 * @param type
	 *            the Exception's message tag
	 * @param itemType
	 *            the item's type
	 * @param name
	 *            the item's name
	 * @param id
	 *            the item's ID
	 */
	public NotFound(MessageType type, ItemType itemType, String name, long id) {
		super(type, itemType, name, id, PreparedMessage.NotFound);
	}
}
