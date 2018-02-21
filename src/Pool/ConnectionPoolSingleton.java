package Pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import Exceptions.MessageType;
import Exceptions.General.ConnectionPoolError;

/**
 * Get Connections to the SQL DaraBase by singleton pool of connections
 */
public class ConnectionPoolSingleton {

	public static final int MAX_CONNECTIONS = 10;
	private static final String dbUrl = "jdbc:mysql://localhost:3306/coupon_db", dbUser = "root", dbPassword = "123456";
	private static ConnectionPoolSingleton instance = null;
	private static Collection<Connection> pool = new ArrayList<Connection>();
	private static Collection<Connection> connections = new ArrayList<Connection>();

	// a private constructor
	private ConnectionPoolSingleton() {
		// Initialize the Connection Pool with a list of Connections up to the MAX
		// number of connections
		try {

			for (int i = 0; i < MAX_CONNECTIONS; i++) {
				pool.add(createConnection());
			}
		}

		catch (ConnectionPoolError e) {
			System.err.println(e.getMessage());
		}
	}

	// CREATE a Single Instance of CONNECTION POOL Object
	/**
	 * Create a singleton instance of a connection pool
	 * 
	 * @return The singleton instance
	 */
	public static final ConnectionPoolSingleton getInstance() {

		if (instance == null) {

			instance = new ConnectionPoolSingleton();
		}

		return instance;
	}

	// GET A CONNECTION with the SQL DataBase from the Connection Pool if it isn't
	// empty - otherwise waiting for it to get filled
	/**
	 * Get a Connection from the connection pool
	 */
	public synchronized Connection getConnection() throws ConnectionPoolError {

		try {
			// waiting if the connection pool is empty
			while (pool.isEmpty()) {

				System.out.println("     PoolSingleton: THE CONNECTION POOL IS EMPTY - WAITING");
				wait();
			}

			// getting a connection and remove it from the pool list
			// System.out.println(" getting a connection");
			Connection connection = pool.iterator().next();
			pool.remove(connection);
			// System.out.println("PoolSingleton: After Get Connection. pool size: " +
			// getSize());
			connections.add(connection);
			notifyAll();
			return connection;
		}

		catch (Exception e) {

			throw new ConnectionPoolError(MessageType.ERR, 
					"An Error occured while trying to GET a connection", e.getMessage());
		}
	}

	// a Method that add a given connection to the Connection Pool if the pool isn't
	// full - otherwise waiting for it to get empty
	/**
	 * Return a Connection to the connection pool
	 */
	public synchronized void returnConnection(Connection conn) throws ConnectionPoolError {

		try {
			// waiting if the connection pool is full
			while (pool.size() >= MAX_CONNECTIONS) {
				
				System.out.println("  PoolSingleton: THE CONNECTION POOL IS FULL - WAITING");
				closeAllConnections();
				wait();
			}
			// if the pool isn't full - unlock the waiting list of connections and add the
			// given connection to the pool
			// System.out.println(" adding connection to the pool");
			pool.add(conn);
			// System.out.println("PoolSingleton: After Return Connection. pool size: " +
			// getSize());
			connections.remove(conn);
			notifyAll();
		}

		catch (InterruptedException e) {

			throw new ConnectionPoolError(MessageType.ERR, 
					"An Error occured while trying to RETURN a connection", e.getMessage());
		}
	}

	// closing all connections
	/**
	 * A static method that Close all the open Connections
	 */
	public static void closeAllConnections() throws ConnectionPoolError {

		try {

			for (Connection connection : pool) {

				connection.close();
			}

			for (Connection connection : connections) {

				connection.close();
			}
		}

		catch (SQLException e) {

			throw new ConnectionPoolError(MessageType.ERR, 
					"An Error occured while trying to CLOSE ALL connections", e.getMessage());
		}
	}

	// CREATE a new CONNECTION with the SQL DataBase
	private Connection createConnection() throws ConnectionPoolError {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection;
			connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			return connection;
		} catch (Exception e) {
			throw new ConnectionPoolError(MessageType.ERR, 
					"An Error occured while trying to CREATE a connection", e.getMessage());
		}
	}

	// get the Connection Pool size - an internal method for debugging
	@SuppressWarnings("unused")
	private int getSize() {
		return pool.size();
	}
}
