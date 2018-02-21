package Exceptions.DBError;

public final class DBStatementError extends DBError{
	public DBStatementError(String sqlMessage) {
		super("Statement error", "Unable to close statement for an uknown reason", sqlMessage);
	}
}
