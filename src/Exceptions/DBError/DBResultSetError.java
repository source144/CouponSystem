package Exceptions.DBError;

public final class DBResultSetError extends DBError{
	public DBResultSetError(String sqlMessage) {
		super("ResultSet error", "Unable to use ResultSet after statement closed.", sqlMessage);
	}
}
