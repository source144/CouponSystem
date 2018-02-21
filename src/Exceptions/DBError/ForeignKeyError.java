package Exceptions.DBError;

public final class ForeignKeyError extends DBError {
	private String key, sqlLine;

	public ForeignKeyError(String key, String sqlLine, String details) {
		super("Foreign key SQL error: " + key, "Statement '" + sqlLine +"' returned an error due to FOREIGN KEY conflic with key: '" + key + "'", details);
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
