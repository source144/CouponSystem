package Exceptions.ListEmpty;

import Exceptions.CouponSystemException;
import Exceptions.ItemType;
import Exceptions.MessageType;

/**
 * An Empty Filtered Item List exception
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 *
 */
public final class EmptyFilteredList extends CouponSystemException implements EmptyListException {
	private final String query;
	private final ItemType iType;
	

	/**
	 * Constructs an EmptyFilteredItemList Exception with a specified filter
	 * 
	 * @param type
	 *            the type of exception
	 * @param itemType
	 *            the list's Item type
	 * @param filter
	 *            the list's filter
	 */
	public EmptyFilteredList(MessageType type, ItemType itemType, String filter) {
		super(type, "Couldn't find any " +  itemType.pluralLower() + " in database.", "No " + itemType.pluralLower() + " entries found in database for search filter=(" + filter + ").");
		this.iType = itemType;
		this.query = filter;
	}
	
	/**
	 * Constructs an EmptyFilteredItemList Exception without a specified filter
	 * 
	 * @param type
	 *            the type of exception
	 * @param itemType
	 *            the list's Item type
	 */
	public EmptyFilteredList(MessageType type, ItemType itemType) {
		this(type, itemType, "UNSPECIFIED");
	}

	@Override
	public ItemType getItemType() {
		return this.iType;
	}
	@Override
	public String getSearchQuery() {
		return this.query;
	}
}
