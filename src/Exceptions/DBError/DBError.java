package Exceptions.DBError;

public class DBError extends Exception {
	private final String details, sqlErrorDetails;

	public DBError(String error, String details, String sqlErrorDetails) {
		super(error);
		this.details = details;
		this.sqlErrorDetails = sqlErrorDetails;
	}

	public final String getSqlErrorDetails() {
		return this.sqlErrorDetails;
	}

	public final String getDetails() {
		return this.details;
	}
}
