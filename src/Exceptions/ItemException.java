package Exceptions;

/**
 * An Exception of an item in the Coupon system
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 */
public abstract class ItemException extends CouponSystemException {

	private final ItemType itemType;
	private final String name;
	private final long id;

	/**
	 * Constructs a new ItemException
	 * 
	 * @param type
	 *            the Exception's MessageType
	 * @param itemType
	 *            the item's ItemType
	 * @param name
	 *            the item's name
	 * @param id
	 *            the item's ID
	 * @param message
	 *            the Exception's PreparedMessage
	 * @param details
	 *            detailed error message
	 */
	public ItemException(MessageType type, ItemType itemType, String name, long id, PreparedMessage message,
			String details) {
		super(type, itemType + " " + message, details);
		this.name = (name == null)?"":name;
		this.id = (id<1)?-1:id;
		this.itemType = itemType;
	}

	/**
	 * Constructs a new ItemException with the appropriate details.
	 * 
	 * @param type
	 *            the Exception's MessageType
	 * @param itemType
	 *            the item's ItemType
	 * @param name
	 *            the item's name
	 * @param id
	 *            the item's ID
	 * @param message
	 *            the Exception's PreparedMessage
	 */
	public ItemException(MessageType type, ItemType itemType, String name, long id, PreparedMessage message) {
			this(type, itemType, name, id, message, itemType + quoteName(name) + printableId(id) + message);
	}

	/**
	 * Gets the name of this Exception's item *
	 * 
	 * @return returns the name
	 */
	final public String getItemName() {
		return this.name;
	}

	/**
	 * Gets the ID of this Exception's item
	 * 
	 * @return returns the ID
	 */
	final public long getID() {
		return this.id;
	}

	/**
	 * Gets this Exception's item's ItemType
	 * 
	 * @return the ItemType
	 */
	final public ItemType getItemType() {
		return this.itemType;
	}
	
	/**
	 * Quotes a name
	 * @param name the name
	 * @return either (1) returns a quoted name (<em>Gonen -> <strong>'Gonen' </strong></em>) or (2) returns an empty {@link String} if the name given is empty or null.
	 */
	private static String quoteName(String name) {
		return (name == null || name.equals("")) ? "" : " '" + name + "'";
	}
	
	/**
	 * Makes an ID printable
	 * @param id the id
	 * @return either (1) returns a printable id (i.e <em>9 -> <strong>[ID=9]</strong></em>) or (2) returns a single space if the id given is 0 or lower.
	 */
	private static String printableId(long id) {
		return (id < 1) ? " " : " [ID=" + id + "] ";
	}
}
