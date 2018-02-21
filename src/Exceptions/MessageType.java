package Exceptions;

/**
 * An ENUM of all Message Types
 * 
 * @author Gonen Matias
 * @version 1.0 30/01/2018
 */
public enum MessageType {
	WARN, ERR, NOTE;

	/**
	 * Gets the TAG String value of this MessageType
	 * 
	 * @return the TAG String value
	 */
	@Override
	public String toString() {
		switch (this) {
		case WARN:
			return "WARN";
		case ERR:
			return "ERR";
		default:
			return "NOTE";
		}
	}

	/**
	 * Gets the long String value of this MessageType
	 * 
	 * @return the long String value
	 */
	public final String defLong() {
		switch (this) {
		case WARN:
			return "Warning";
		case ERR:
			return "Error";
		default:
			return "Notification";
		}
	}

	/**
	 * Gets the uppercased long String value of this MessageType
	 * 
	 * @return the uppercased long String value
	 */
	public final String upperLong() {
		switch (this) {
		case WARN:
			return "WARNING";
		case ERR:
			return "ERROR";
		default:
			return "NOTIFICATION";
		}
	}

}
