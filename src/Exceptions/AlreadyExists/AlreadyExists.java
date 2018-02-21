package Exceptions.AlreadyExists;

import Exceptions.ItemException;
import Exceptions.ItemType;
import Exceptions.MessageType;
import Exceptions.PreparedMessage;

/**
 * A Already Exists ItemException in the Coupon system
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 *
 */
public abstract class AlreadyExists extends ItemException {

	/**
	 * Constructs a new AlreadyExists ItemException with a specified error
	 * description
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
	public AlreadyExists(MessageType type, ItemType itemType, String name, long id, String details) {
		super(type, itemType, name, id, PreparedMessage.AlreadyExists, details);
	}

	/**
	 * Constructs a new AlreadyExists ItemException
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
	public AlreadyExists(MessageType type, ItemType itemType, String name, long id) {
		super(type, itemType, name, id, PreparedMessage.AlreadyExists);
	}
}
