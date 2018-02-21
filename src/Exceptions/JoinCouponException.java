package Exceptions;

import Bean.Coupon;

/**
 * An Exception of a relationship between an Item and a {@link Coupon} in the Coupon system
 * 
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 */
public abstract class JoinCouponException extends CouponSystemException {

	final String couponTitle, itemName;
	final long couponId, itemId;
	final ItemType itemType;

	/**
	 * Constructs a new {@link JoinCouponException}
	 * 
	 * @param itemType
	 *            the item's type
	 * @param itemName
	 *            the item's name
	 * @param itemId
	 *            the item's ID
	 * @param couponTitle
	 *            the title of the Coupon
	 * @param couponId
	 *            the ID of the Coupon
	 * @param message
	 *            the Exception's PreparedMessage
	 * @param details
	 *            detailed error message
	 */
	public JoinCouponException(MessageType type, ItemType itemType, String itemName, long itemId, String couponTitle, long couponId, PreparedMessage message,
			String details) {
		super(type, itemType + " '" + itemName + "' " + message + " " +  ItemType.COUPON + " '" + couponTitle + "'.", details);
		this.couponTitle = couponTitle;
		this.itemName = itemName;
		this.couponId = couponId;
		this.itemId = itemId;
		this.itemType = itemType;
	}

	/**
	 * Constructs a new {@link JoinCouponException}
	 * 
	 * @param itemType
	 *            the item's type
	 * @param itemName
	 *            the item's name
	 * @param itemId
	 *            the item's ID
	 * @param couponTitle
	 *            the title of the Coupon
	 * @param couponId
	 *            the ID of the Coupon
	 * @param message
	 *            the Exception's PreparedMessage
	 * @param details
	 *            detailed error message
	 */
	public JoinCouponException(MessageType type, ItemType itemType, String itemName, long itemId, String couponTitle, long couponId, PreparedMessage message) {
		this(type, itemType, itemName, itemId, couponTitle, couponId, message, itemType + " '" + itemName + "' (ID=" + itemId
				+ ") " + message  + " " + ItemType.COUPON + " '" + couponTitle + "' (ID=" + couponId + ").");
	}

	/**
	 * Gets Coupon's PRIMARY KEY (ID)
	 * 
	 * @return the Coupon's ID
	 */
	public final long getCouponId() {
		return this.couponId;
	}

	/**
	 * Gets Coupon's title
	 * 
	 * @return the Coupon's title
	 */
	public final String getCouponTitle() {
		return this.couponTitle;
	}

	/**
	 * Gets the Item's PRIMARY KEY (ID)
	 * 
	 * @return the Item's ID
	 */
	public final long getItemId() {
		return this.itemId;
	}

	/**
	 * Gets the Item's name
	 * 
	 * @return the Item's name
	 */
	public final String getItemName() {
		return this.itemName;
	}

	/**
	 * Gets Item's ItemType
	 * 
	 * @return the Item's ItemType
	 */
	public final ItemType getItemType() {
		return this.itemType;
	}
}
