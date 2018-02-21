package Exceptions;

/**
 * An ENUM of prepared messages for specific errors.
 * 
 * @author Gonen Matias
 * @version 1.0 02/02/2018
 *
 */
public enum PreparedMessage {
	AlreadyExists("already exists in database."), NotFound("not found in database."), StockEmpty(
			"is out of stock."), Expired("has expired."), Owns("already owns"), NotOwn("doesn't own");

	final String message;

	/**
	 * Constructs a new PreparedMessage
	 * 
	 * @param message
	 *            the String value of this PreparedMessage
	 */
	private PreparedMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return this.message;
	}
}
