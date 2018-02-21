package Exceptions.ListEmpty;

import Exceptions.ItemType;

/**
 * An interface for empty list exceptions
 * @author Gonen Matias
 * @version 1.0 01/02/2018
 *
 */
public interface EmptyListException {

	/**
	 * Gets Item type of this list 
	 * @return the ItemType
	 */
	public ItemType getItemType();

	/**
	 * Get the search query of this list
	 * @return the search query
	 */
	public String getSearchQuery();
}
