package Exceptions.ListEmpty;

import Exceptions.CouponSystemException;
import Exceptions.ItemType;
import Exceptions.MessageType;

/**
 * An Empty Item List exception
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 *
 */
public final class EmptyItemList extends CouponSystemException implements EmptyListException {
	private String query;
	private final ItemType iType;

	/**
	 * Constructs an EmptyItemList Exception
	 * 
	 * @param type
	 *            the type of exception
	 * @param itemType
	 *            the list's Item type
	 */
	public EmptyItemList(MessageType type, ItemType itemType) {
		super(type, "Couldn't find any " + itemType.pluralLower() + " in database.",
				"No " + itemType.pluralLower() + " entries found in database.");
		this.iType = itemType;
		this.query = "ALL";
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
