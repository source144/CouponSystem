package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Exceptions.DBError.DBError;
import Exceptions.DBError.DBStatementError;
import Exceptions.DBError.DBUknownError;
import Exceptions.DBError.DuplicateKeyError;
import Exceptions.DBError.ForeignKeyError;
import LogHandler.CouponLogHandler;
import Pool.ConnectionPoolSingleton;

/**
 * Database utility that handles statements, requests and all the manipulation
 * of data on the database.
 * 
 * @author Gonen Matias
 * @version 1.0 30/01/2018
 */
public abstract class UtilDBDAO {
	protected static final ConnectionPoolSingleton pool = ConnectionPoolSingleton.getInstance();
	protected static final CouponLogHandler handler = CouponLogHandler.getInstance();
	protected static final String tbl_coupon = "coupon";
	protected static final String tbl_company = "company";
	protected static final String tbl_customer = "customer";
	protected static final String tbl_join_company = "company_coupon";
	protected static final String tbl_join_customer = "customer_coupon";
	protected static final String archive_coupon = "archive_coupon";
	protected static final String archive_join_customer = "archive_customer_coupon";
	protected static final String archive_join_company = "archive_company_coupon";
	protected static final String FK_COMPANY_ID = "FK_COMPANY_ID";
	protected static final String FK_CUSTOMER_ID = "FK_CUSTOMER_ID";
	protected static final String FK_COUPON_ID = "FK_COUPON_ID";
	protected static final String FK_ARCHIVE_COUPON = "FK_ARCHIVE_COUPON";
	protected static final String FK_ARCHIVE_CUSTOMER = "FK_ARCHIVE_CUSTOMER";
	protected static final String FK_ARCHIVE_COMPANY = "FK_ARCHIVE_COMPANY";
	protected static final String JOIN_COUPON_KEY = "COUPON_ID";
	protected static final String JOIN_COMPANY_KEY = "COMP_ID";
	protected static final String JOIN_CUSTOMER_KEY = "CUST_ID";
	protected static final String COUPON_KEY_ARCHIVE = "OLD_ID";
	protected static final String COUPON_KEY = "ID";
	protected static final String COUPON_TITLE = "TITLE";
	protected static final String COUPON_START = "START_DATE";
	protected static final String COUPON_END = "END_DATE";
	protected static final String COUPON_AMOUNT = "AMOUNT";
	protected static final String COUPON_TYPE = "TYPE";
	protected static final String COUPON_MESSAGE = "MESSAGE";
	protected static final String COUPON_PRICE = "PRICE";
	protected static final String COUPON_IMAGE = "IMAGE";
	protected static final String COMPANY_KEY = "ID";
	protected static final String COMPANY_NAME = "COMP_NAME";
	protected static final String COMPANY_EMAIL = "EMAIL";
	protected static final String COMPANY_PASSWORD = "PASSWORD";
	protected static final String CUSTOMER_KEY = "ID";
	protected static final String CUSTOMER_NAME = "CUST_NAME";
	protected static final String CUSTOMER_PASSWORD = "PASSWORD";
	protected List<Statement> statementsToClose;

	/**
	 * Creates a new instance of Database utility
	 */
	public UtilDBDAO() {
		statementsToClose = new ArrayList<>();
	}

	/**
	 * Executes the SQL statement in database
	 * 
	 * @param conn
	 *            the Connection to database
	 * @param sqlLine
	 *            the SQL statement to be executed
	 * @param args
	 *            the arguments to be inserted into the statement, can be null if
	 *            there are no arguments.
	 * @return either (1) the row count for SQL Data Manipulation Language (DML)
	 *         statements or (2) 0 for SQL statements that return nothing
	 * @throws DuplicateKeyError
	 *             if data couldn't be manipulated due to DUPLICATE KEY restrictions
	 * @throws ForeignKeyError
	 *             if data couldn't be manipulated due to FOREIGN KEY restrictions
	 * @throws DBError
	 *             if a database access error occurs
	 * @throws IllegalArgumentException
	 *             if the Connection or SQL line arguments aren't valid
	 */
	protected static final int runStatement(Connection conn, String sqlLine, Object[] args)
			throws DBError, IllegalArgumentException {
		PreparedStatement statement = null;
		int result = 0;

		try {
			if (sqlLine == null || sqlLine.equals("") || conn == null) {
				throw new IllegalArgumentException("the sqlLine is null or empty, or the connection is null");
			}
			statement = (PreparedStatement) conn.prepareStatement(sqlLine);
			if (args != null)
				for (int i = 0; i < args.length; i++)
					statement.setObject(i + 1, args[i]);

			result = statement.executeUpdate();
		} catch (SQLException e) {
			if (e.getMessage().contains("Duplicate")) {
				throw new DuplicateKeyError(sqlLine, e.getMessage());
			} else if (e.getMessage().contains("CONSTRAINT")) {
				if (e.getMessage().contains(FK_CUSTOMER_ID)) {
					throw new ForeignKeyError(FK_CUSTOMER_ID, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_COUPON_ID)) {
					throw new ForeignKeyError(FK_COUPON_ID, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_COMPANY_ID)) {
					throw new ForeignKeyError(FK_COMPANY_ID, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_ARCHIVE_COUPON)) {
					throw new ForeignKeyError(FK_ARCHIVE_COUPON, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_ARCHIVE_CUSTOMER)) {
					throw new ForeignKeyError(FK_ARCHIVE_CUSTOMER, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_ARCHIVE_COMPANY)) {
					throw new ForeignKeyError(FK_ARCHIVE_COMPANY, sqlLine, e.getMessage());
				} else
					throw new ForeignKeyError("UNKNOWN", sqlLine, e.getMessage());
			} else {
				throw new DBUknownError(sqlLine, e.getMessage());
			}
		} finally {
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				throw new DBStatementError(e.getMessage());
			}
		}
		return result;
	}

	/**
	 * Executes the SQL statement in database
	 * 
	 * @param conn
	 *            the Connection to database
	 * @param sqlLine
	 *            the SQL statement to be executed
	 * @return either (1) the row count for SQL Data Manipulation Language (DML)
	 *         statements or (2) 0 for SQL statements that return nothing
	 * @throws DuplicateKeyError
	 *             if data couldn't be manipulated due to DUPLICATE KEY restrictions
	 * @throws ForeignKeyError
	 *             if data couldn't be manipulated due to FOREIGN KEY restrictions
	 * @throws DBError
	 *             if a database access error occurs
	 * @throws IllegalArgumentException
	 *             if the Connection or SQL line arguments aren't valid
	 */
	protected static final int runStatement(Connection conn, String sqlLine) throws DBError, IllegalArgumentException {
		return runStatement(conn, sqlLine, null);
	}

	/**
	 * Executes the SQL statement in database and returns it's output
	 * 
	 * @param conn
	 *            the Connection to database
	 * @param sqlLine
	 *            the SQL statement to be executed
	 * @param args
	 *            the arguments to be inserted into the statement, can be null if
	 *            there are no arguments.
	 * @return returns the database's output as a ResultSet
	 * @throws DuplicateKeyError
	 *             if data couldn't be manipulated due to DUPLICATE KEY restrictions
	 * @throws ForeignKeyError
	 *             if data couldn't be manipulated due to FOREIGN KEY restrictions
	 * @throws DBError
	 *             if a database access error occurs
	 * @throws IllegalArgumentException
	 *             if the Connection or SQL line arguments aren't valid
	 */
	protected final ResultSet getResult(Connection conn, String sqlLine, Object[] args)
			throws DBError, IllegalArgumentException {
		PreparedStatement statement = null;

		try {
			if (sqlLine == null || sqlLine.equals("") || conn == null) {
				throw new IllegalArgumentException("the sqlLine is null or empty, or the connection is null");
			}
			statement = (PreparedStatement) conn.prepareStatement(sqlLine);
			if (args != null)
				for (int i = 0; i < args.length; i++)
					statement.setObject(i + 1, args[i]);
			return statement.executeQuery();
		} catch (SQLException e) {
			if (e.getMessage().contains("Duplicate"))
				throw new DuplicateKeyError(sqlLine, e.getMessage());
			else if (e.getMessage().contains("CONSTRAINT")) {
				if (e.getMessage().contains(FK_CUSTOMER_ID)) {
					throw new ForeignKeyError(FK_CUSTOMER_ID, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_COUPON_ID)) {
					throw new ForeignKeyError(FK_COUPON_ID, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_COMPANY_ID)) {
					throw new ForeignKeyError(FK_COMPANY_ID, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_ARCHIVE_COUPON)) {
					throw new ForeignKeyError(FK_ARCHIVE_COUPON, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_ARCHIVE_CUSTOMER)) {
					throw new ForeignKeyError(FK_ARCHIVE_CUSTOMER, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_ARCHIVE_COMPANY)) {
					throw new ForeignKeyError(FK_ARCHIVE_COMPANY, sqlLine, e.getMessage());
				} else
					throw new ForeignKeyError("UNKNOWN", sqlLine, e.getMessage());
			} else
				throw new DBUknownError(sqlLine, e.getMessage());
		} finally {
			if (statement != null)
				statementsToClose.add(statement);
		}
	}

	/**
	 * Executes the SQL statement in database and returns it's output
	 * 
	 * @param conn
	 *            the Connection to database
	 * @param sqlLine
	 *            the SQL statement to be executed
	 * @return returns the database's output as a ResultSet
	 * @throws DuplicateKeyError
	 *             if data couldn't be manipulated due to DUPLICATE KEY restrictions
	 * @throws ForeignKeyError
	 *             if data couldn't be manipulated due to FOREIGN KEY restrictions
	 * @throws DBError
	 *             if a database access error occurs
	 * @throws IllegalArgumentException
	 *             if the Connection or SQL line arguments aren't valid
	 */
	protected final ResultSet getResult(Connection conn, String sqlLine) throws DBError, IllegalArgumentException {
		return getResult(conn, sqlLine, null);
	}

	/**
	 * Executes an INSERT SQL statement in database and returns the PRIMARY KEY
	 * Object of the new record
	 * 
	 * @param conn
	 *            the Connection to database
	 * @param sqlLine
	 *            the SQL statement to be executed, MUST be an INSERT statement
	 * @param args
	 *            the arguments to be inserted into the statement, can be null if
	 *            there are no arguments.
	 * @return either (1) returns the PRIMARY KEY Object of the new record or (2)
	 *         null if no data has been inserted
	 * @throws DuplicateKeyError
	 *             if data couldn't be manipulated due to DUPLICATE KEY restrictions
	 * @throws ForeignKeyError
	 *             if data couldn't be manipulated due to FOREIGN KEY restrictions
	 * @throws DBError
	 *             if a database access error occurs
	 * @throws IllegalArgumentException
	 *             if the Connection or SQL line arguments aren't valid
	 */
	protected static final Object insertAndGetKey(Connection conn, String sqlLine, Object[] args)
			throws DBError, IllegalArgumentException {
		PreparedStatement statement = null;
		ResultSet keys = null;

		try {
			if (sqlLine == null || sqlLine.equals("") || conn == null)
				throw new IllegalArgumentException("the sqlLine is null or empty, or the connection is null");
			if (!sqlLine.startsWith("INSERT"))
				throw new IllegalArgumentException("The sqlLine action isn't INSERT");

			statement = (PreparedStatement) conn.prepareStatement(sqlLine, Statement.RETURN_GENERATED_KEYS);

			if (args != null)
				for (int i = 0; i < args.length; i++)
					statement.setObject(i + 1, args[i]);

			statement.executeUpdate();
			keys = statement.getGeneratedKeys();
			if (keys.next())
				return keys.getObject(1);
		} catch (SQLException e) {
			if (e.getMessage().contains("Duplicate")) {
				throw new DuplicateKeyError(sqlLine, e.getMessage());
			} else if (e.getMessage().contains("CONSTRAINT")) {
				if (e.getMessage().contains(FK_CUSTOMER_ID)) {
					throw new ForeignKeyError(FK_CUSTOMER_ID, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_COUPON_ID)) {
					throw new ForeignKeyError(FK_COUPON_ID, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_COMPANY_ID)) {
					throw new ForeignKeyError(FK_COMPANY_ID, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_ARCHIVE_COUPON)) {
					throw new ForeignKeyError(FK_ARCHIVE_COUPON, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_ARCHIVE_CUSTOMER)) {
					throw new ForeignKeyError(FK_ARCHIVE_CUSTOMER, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_ARCHIVE_COMPANY)) {
					throw new ForeignKeyError(FK_ARCHIVE_COMPANY, sqlLine, e.getMessage());
				} else
					throw new ForeignKeyError("UNKNOWN", sqlLine, e.getMessage());
			} else {
				throw new DBUknownError(sqlLine, e.getMessage());
			}
		} finally {
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				throw new DBStatementError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * Executes an INSERT SQL statement in database and returns the PRIMARY KEY
	 * Object of the new record
	 * 
	 * @param conn
	 *            the Connection to database
	 * @param sqlLine
	 *            the SQL statement to be executed, MUST be an INSERT statement
	 * @return either (1) returns the PRIMARY KEY Object of the new record or (2)
	 *         null if no data has been inserted
	 * @throws DuplicateKeyError
	 *             if data couldn't be manipulated due to DUPLICATE KEY restrictions
	 * @throws ForeignKeyError
	 *             if data couldn't be manipulated due to FOREIGN KEY restrictions
	 * @throws DBError
	 *             if a database access error occurs
	 * @throws IllegalArgumentException
	 *             if the Connection or SQL line arguments aren't valid
	 */
	protected static final Object insertAndGetKey(Connection conn, String sqlLine)
			throws DBError, IllegalArgumentException {
		return insertAndGetKey(conn, sqlLine, null);
	}

	/**
	 * Executes an INSERT SQL statement in database and returns the PRIMARY KEY of
	 * the new record
	 * 
	 * @param conn
	 *            the Connection to database
	 * @param sqlLine
	 *            the SQL statement to be executed, MUST be an INSERT statement
	 * @param args
	 *            the arguments to be inserted into the statement, can be null if
	 *            there are no arguments.
	 * @return either (1) returns the PRIMARY KEY of the new record or (2) null if
	 *         no data has been inserted
	 * @throws DuplicateKeyError
	 *             if data couldn't be manipulated due to DUPLICATE KEY restrictions
	 * @throws ForeignKeyError
	 *             if data couldn't be manipulated due to FOREIGN KEY restrictions
	 * @throws DBError
	 *             if a database access error occurs
	 * @throws IllegalArgumentException
	 *             if the Connection or SQL line arguments aren't valid
	 */
	protected static final long insertAndGetLongKey(Connection conn, String sqlLine, Object[] args)
			throws DBError, IllegalArgumentException {
		PreparedStatement statement = null;
		ResultSet keys = null;

		try {
			if (sqlLine == null || sqlLine.equals("") || conn == null)
				throw new IllegalArgumentException("the sqlLine is null or empty, or the connection is null");
			if (!sqlLine.startsWith("INSERT"))
				throw new IllegalArgumentException("The sqlLine action isn't INSERT");

			statement = (PreparedStatement) conn.prepareStatement(sqlLine, Statement.RETURN_GENERATED_KEYS);

			if (args != null)
				for (int i = 0; i < args.length; i++)
					statement.setObject(i + 1, args[i]);

			statement.executeUpdate();
			keys = statement.getGeneratedKeys();
			if (keys.next())
				return keys.getLong(1);
		} catch (SQLException e) {
			if (e.getMessage().contains("Duplicate")) {
				throw new DuplicateKeyError(sqlLine, e.getMessage());
			} else if (e.getMessage().contains("CONSTRAINT")) {
				if (e.getMessage().contains(FK_CUSTOMER_ID)) {
					throw new ForeignKeyError(FK_CUSTOMER_ID, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_COUPON_ID)) {
					throw new ForeignKeyError(FK_COUPON_ID, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_COMPANY_ID)) {
					throw new ForeignKeyError(FK_COMPANY_ID, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_ARCHIVE_COUPON)) {
					throw new ForeignKeyError(FK_ARCHIVE_COUPON, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_ARCHIVE_CUSTOMER)) {
					throw new ForeignKeyError(FK_ARCHIVE_CUSTOMER, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_ARCHIVE_COMPANY)) {
					throw new ForeignKeyError(FK_ARCHIVE_COMPANY, sqlLine, e.getMessage());
				} else
					throw new ForeignKeyError("UNKNOWN", sqlLine, e.getMessage());
			} else {
				throw new DBUknownError(sqlLine, e.getMessage());
			}
		} finally {
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				throw new DBStatementError(e.getMessage());
			}
		}
		return -1;
	}

	/**
	 * Executes an INSERT SQL statement in database and returns the PRIMARY KEY of
	 * the new record
	 * 
	 * @param conn
	 *            the Connection to database
	 * @param sqlLine
	 *            the SQL statement to be executed, MUST be an INSERT statement
	 * @return either (1) returns the PRIMARY KEY of the new record or (2) null if
	 *         no data has been inserted
	 * @throws DuplicateKeyError
	 *             if data couldn't be manipulated due to DUPLICATE KEY restrictions
	 * @throws ForeignKeyError
	 *             if data couldn't be manipulated due to FOREIGN KEY restrictions
	 * @throws DBError
	 *             if a database access error occurs
	 * @throws IllegalArgumentException
	 *             if the Connection or SQL line arguments aren't valid
	 */
	protected static final long insertAndGetLongKey(Connection conn, String sqlLine)
			throws DBError, IllegalArgumentException {
		return insertAndGetLongKey(conn, sqlLine, null);
	}

	/**
	 * Creates and executes the SQL statement and the database's output as a
	 * ResultSet of the specified fields
	 * 
	 * @param conn
	 *            the Connection to database
	 * @param sqlLine
	 *            the SQL statement to be executed
	 * @param args
	 *            the arguments to be inserted into the statement, can be null if
	 *            there are no arguments.
	 * @param fields
	 *            an array of the fields requested from the database's output,
	 *            returns all fields if null.
	 * @return returns the database's output as a ResultSet of the specified fields
	 * @throws DuplicateKeyError
	 *             if data couldn't be manipulated due to DUPLICATE KEY restrictions
	 * @throws ForeignKeyError
	 *             if data couldn't be manipulated due to FOREIGN KEY restrictions
	 * @throws DBError
	 *             if a database access error occurs
	 * @throws IllegalArgumentException
	 *             if the Connection, SQL line or one of the field's arguments
	 *             aren't valid
	 */
	protected final ResultSet runOutputStatement(Connection conn, String sqlLine, Object[] args, String[] fields)
			throws DBError, IllegalArgumentException {
		PreparedStatement statement = null;

		if (sqlLine == null || sqlLine.equals("") || conn == null)
			throw new IllegalArgumentException("the sqlLine is null or empty, or the connection is null");
		if (fields != null) {
			if (fields[0] == null || fields[0].equals("") || fields[0].contains(" "))
				throw new IllegalArgumentException("Requested field either empty or invalid");
			else {
				sqlLine += " OUTPUT INSERTED." + fields[0];
				for (int i = 1; i < fields.length; i++) {
					if (fields[i] == null || fields[i].equals("") || fields[i].contains(" "))
						throw new IllegalArgumentException("Requested field either empty or invalid (index " + i + ")");
					sqlLine += ", INSERTED." + fields[i];
				}
			}
		} else
			sqlLine += " OUTPUT INSERTED.*";

		try {

			statement = (PreparedStatement) conn.prepareStatement(sqlLine);
			if (args != null)
				for (int i = 0; i < args.length; i++)
					statement.setObject(i + 1, args[i]);

			statement.executeUpdate();
			return statement.getResultSet();
		} catch (SQLException e) {
			if (e.getMessage().contains("Duplicate")) {
				throw new DuplicateKeyError(sqlLine, e.getMessage());
			} else if (e.getMessage().contains("CONSTRAINT")) {
				if (e.getMessage().contains(FK_CUSTOMER_ID)) {
					throw new ForeignKeyError(FK_CUSTOMER_ID, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_COUPON_ID)) {
					throw new ForeignKeyError(FK_COUPON_ID, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_COMPANY_ID)) {
					throw new ForeignKeyError(FK_COMPANY_ID, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_ARCHIVE_COUPON)) {
					throw new ForeignKeyError(FK_ARCHIVE_COUPON, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_ARCHIVE_CUSTOMER)) {
					throw new ForeignKeyError(FK_ARCHIVE_CUSTOMER, sqlLine, e.getMessage());
				} else if (e.getMessage().contains(FK_ARCHIVE_COMPANY)) {
					throw new ForeignKeyError(FK_ARCHIVE_COMPANY, sqlLine, e.getMessage());
				} else
					throw new ForeignKeyError("UNKNOWN", sqlLine, e.getMessage());
			} else {
				throw new DBUknownError(sqlLine, e.getMessage());
			}
		} finally {
			if (statement != null)
				statementsToClose.add(statement);
		}
	}

	/**
	 * Creates and executes the SQL statement and the database's output as a
	 * ResultSet of the specified fields
	 * 
	 * @param conn
	 *            the Connection to database
	 * @param sqlLine
	 *            the SQL statement to be executed
	 * @param fields
	 *            an array of the fields requested from the database's output,
	 *            returns all fields if null.
	 * @return returns the database's output as a ResultSet of the specified fields
	 * @throws DuplicateKeyError
	 *             if data couldn't be manipulated due to DUPLICATE KEY restrictions
	 * @throws ForeignKeyError
	 *             if data couldn't be manipulated due to FOREIGN KEY restrictions
	 * @throws DBError
	 *             if a database access error occurs
	 * @throws IllegalArgumentException
	 *             if the Connection, SQL line or one of the field's arguments
	 *             aren't valid
	 */
	protected final ResultSet runOutputStatement(Connection conn, String sqlLine, String[] fields)
			throws DBError, IllegalArgumentException {
		return runOutputStatement(conn, sqlLine, null, fields);
	}

	/**
	 * Creates and executes the SQL statement and the database's output as a
	 * ResultSet of the specified fields
	 * 
	 * @param conn
	 *            the Connection to database
	 * @param sqlLine
	 *            the SQL statement to be executed
	 * @param args
	 *            the arguments to be inserted into the statement, can be null if
	 *            there are no arguments.
	 * @return returns the database's output as a ResultSet of all fields
	 * @throws DuplicateKeyError
	 *             if data couldn't be manipulated due to DUPLICATE KEY restrictions
	 * @throws ForeignKeyError
	 *             if data couldn't be manipulated due to FOREIGN KEY restrictions
	 * @throws DBError
	 *             if a database access error occurs
	 * @throws IllegalArgumentException
	 *             if the Connection, SQL line arguments aren't valid
	 */
	protected final ResultSet runOutputStatement(Connection conn, String sqlLine, Object[] args)
			throws DBError, IllegalArgumentException {
		return runOutputStatement(conn, sqlLine, args, null);
	}

	/**
	 * Creates and executes the SQL statement and the database's output as a
	 * ResultSet of the specified fields
	 * 
	 * @param conn
	 *            the Connection to database
	 * @param sqlLine
	 *            the SQL statement to be executed
	 * @return returns the database's output as a ResultSet of all fields
	 * @throws DuplicateKeyError
	 *             if data couldn't be manipulated due to DUPLICATE KEY restrictions
	 * @throws ForeignKeyError
	 *             if data couldn't be manipulated due to FOREIGN KEY restrictions
	 * @throws DBError
	 *             if a database access error occurs
	 * @throws IllegalArgumentException
	 *             if the Connection, SQL line arguments aren't valid
	 */
	protected final ResultSet runOutputStatement(Connection conn, String sqlLine)
			throws DBError, IllegalArgumentException {
		return runOutputStatement(conn, sqlLine, null, null);
	}

	/**
	 * Closes all the open statements and clears the list<br>
	 * <br>
	 * To access a ResultSets a statement must still be open, once closed the
	 * ResultSet's data is inaccessible.<br>
	 * Therefore, all functions returning a ResultSet leave the statement open and
	 * add it to a list to be closed later when this method is called. <br>
	 * Call this method after you are done with a certain ResultSet to flush the
	 * statement.
	 */
	protected final void closeAllStatements() {
		// ************************ ResultSet closed

		for (Iterator<Statement> iterator = this.statementsToClose.iterator(); iterator.hasNext();) {
			Statement statement = iterator.next();
			try {
				if (statement != null)
					statement.close();

				iterator.remove();
			} catch (SQLException e) { // TODO: LOG/Print to console
				if (e.getMessage().contains("ResultSet")) {
					if (e.getMessage().contains("closed"))
						handler.log("WARN: Couldn't close an open SQL Statement after a ResultSet.\n" + e.getMessage());
					else
						handler.log(
								"WARN: An unexpected error occurred when tried to close an open SQL Statement after a ResultSet.\n"
										+ e.getMessage());
				}
				handler.log("WARN: An unexpected error occurred when tried to close an open SQL Statement.\n"
						+ e.getMessage());
			}
		}
	}

}
