package Exceptions.DBError;

public final class DBUknownError extends DBError {
	private String sqlLine;

	public DBUknownError(String sqlLine, String details) {
		super("Unexpected DB error", "Statement '" + sqlLine + "' returned an unexpected error", details);
		this.sqlLine = sqlLine;
		
	}

	public String getSqlLine() {
		return this.sqlLine;
	}
}
