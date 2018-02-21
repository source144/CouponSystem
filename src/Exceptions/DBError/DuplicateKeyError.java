package Exceptions.DBError;

public final class DuplicateKeyError extends DBError {
	private String key, sqlLine;

	public DuplicateKeyError(String sqlLine, String details) {
		super("Duplicate key error", "Statement '" + sqlLine + "' returned an error due to DUPLICATE KEY conflic",
				details);
		this.key = key;
		this.sqlLine = sqlLine;
	}

	public String getSqlLine() {
		return this.sqlLine;
	}
	
	public String getKey() {
		return this.key;
	}
}
