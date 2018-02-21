package Bean;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * An ENUM of all Coupon Types
 * 
 * @author Gonen Matias
 * @version 1.0 30/01/2018
 */
public enum CouponType {
	RESTAURANTS("Restaurants"), ELECTRONICS("Electronics"), FOOD("Food"), HEALTH("Health"), GAMES("Games"), SPORTS(
			"Sports"), CAMPING("Camping"), TRAVELLING(
					"Travelling"), HOTELS("Hotels"), VACATIONS("Vacations"), TOOLS("Tools"), UNSPECIFIED("Unspecified");

	private final String stringValue;

	/**
	 * Constructs a new Coupon type
	 * 
	 * @param value
	 *            the String value of this CouponType
	 */
	private CouponType(String value) {
		this.stringValue = value;
	}

	private static final Map<String, CouponType> stringValueMap;
	static {
		final Map<String, CouponType> tempMap = Maps.newHashMap();
		for (final CouponType type : CouponType.values()) {
			tempMap.put(type.stringValue.toUpperCase(), type);
		}
		stringValueMap = ImmutableMap.copyOf(tempMap);
	}

	/**
	 * Gets the ENUM value of CouponType from a String value.
	 * 
	 * @param value
	 *            the String value of CouponType
	 * @return either (1) returns CouponType value or (2) returns CouponType
	 *         UNSPECIFIED if value is not a valid CouponType
	 */
	public static final CouponType getEnum(String value) /* throws IllegalArgumentException */ {
		if (!stringValueMap.containsKey(value.toUpperCase()))
			return UNSPECIFIED;
		// throw new IllegalArgumentException("Unknown CouponType value " + value);
		return stringValueMap.get(value.toUpperCase());
	}

	/**
	 * Gets a printable value of this CouponType
	 * 
	 * @return the printable value of this CouponType
	 */
	public String getValue() {
		return this.stringValue;
	}

	@Override
	public String toString() {
		return this.stringValue.toUpperCase();
	}

	/**
	 * Specifies if this CouponType is UNSPECIFIED
	 * 
	 * @return either (1) true if this CouponType is UNSPECIFIED or (2) false if
	 *         this CouponType has a specified value
	 */
	public boolean isUnspecified() {
		return (this == UNSPECIFIED ? true : false);
	}
}
