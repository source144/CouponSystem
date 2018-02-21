package DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Bean.Coupon;
import Bean.CouponType;
import Bean.Customer;
import DAO.CustomerDAO;
import Exceptions.MessageType;
import Exceptions.DBError.DBError;
import Exceptions.DBError.DBStatementError;
import Exceptions.DBError.DuplicateKeyError;
import Exceptions.DBError.ForeignKeyError;
import Exceptions.General.ConnectionPoolError;
import Exceptions.General.UnexpectedError;

/**
 * Customer MySQL DATABASE active data object.
 * 
 * @author Gonen Matias
 * @version 1.0 02/02/2018
 * 
 */
public class CustomerDBDAO extends UtilDBDAO implements CustomerDAO {
	/**
	 * For methods that require statements to stay open and call other methods that
	 * close them.
	 */
	private boolean keepStatementOpen = false;

	/**
	 * Constructs the {@link CustomerDBDAO}.
	 */
	public CustomerDBDAO() {
		super();
	}

	///////////////////////
	// PRIVATE METHODS //
	///////////////////////

	/**
	 * Gets a list of a specific {@link Customer}'s {@link Coupon}s that fit at
	 * least one of the types from the database, <em>either
	 * <strong>archived</strong>, or <strong>NON</strong>-archived</em>.
	 * 
	 * @param fromArchive
	 *            <em><u>true</u></em> for <strong>archived</strong> records, or
	 *            <em><u>false</u></em> for <strong>NON</strong>-archived</em>
	 *            records.
	 * @param types
	 *            the types
	 * @param customer
	 *            the customer
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getCoupons(boolean fromArchive, CouponType[] types, Customer customer)
			throws UnexpectedError {
		if (customer == null)
			throw new UnexpectedError(MessageType.ERR, "Can't get Coupon data for unspecified Customer",
					"Customer is null");

		final String ARCHIVED, KEY;
		ArrayList<Coupon> coupons = new ArrayList<>();
		String selectCouponsQuery = "SELECT * FROM ";
		Object[] selectCouponsArgs = null;
		ResultSet couponsRS = null;

		if (fromArchive) {
			selectCouponsQuery += archive_coupon + " JOIN (SELECT " + JOIN_COUPON_KEY + " FROM " + archive_join_customer
					+ " WHERE " + JOIN_CUSTOMER_KEY + " = " + customer.getId() + ") c ON " + archive_coupon + ".ID = c."
					+ JOIN_COUPON_KEY + "";
			ARCHIVED = "archived ";
			KEY = COUPON_KEY_ARCHIVE;
		} else {
			selectCouponsQuery += tbl_coupon + " JOIN (SELECT " + JOIN_COUPON_KEY + " FROM " + tbl_join_customer
					+ " WHERE " + JOIN_CUSTOMER_KEY + " = " + customer.getId() + ") c ON " + tbl_coupon + ".ID = c."
					+ JOIN_COUPON_KEY + "";
			ARCHIVED = "";
			KEY = COUPON_KEY;
		}

		String errDetail = ARCHIVED + "Coupons";
		if (types != null) {
			if (types.length > 0) {
				selectCouponsArgs = new Object[types.length];
				errDetail = "type filtered " + ARCHIVED + "Coupons";
				selectCouponsQuery += " WHERE TYPE = ?";
				selectCouponsArgs[0] = types[0].toString();
				for (int i = 1; i < types.length; i++) {
					selectCouponsQuery += " OR TYPE = ?";
					selectCouponsArgs[i] = types[i].toString();
				}
			}
		}
		errDetail += " owned by Customer.";

		Connection conn = null;

		try {
			conn = pool.getConnection();
			couponsRS = getResult(conn, selectCouponsQuery, selectCouponsArgs);

			while (couponsRS.next()) {
				try {
					Coupon coupon;
					String type = couponsRS.getString("TYPE");
					final CouponType TYPE = CouponType.getEnum(type);
					if (TYPE.isUnspecified() && !type.equalsIgnoreCase(CouponType.UNSPECIFIED.toString()))
						handler.log("NOTE: Unkown Coupon type.");

					coupon = new Coupon(couponsRS.getLong(KEY), couponsRS.getString("TITLE"),
							couponsRS.getString("MESSAGE"), couponsRS.getString("IMAGE"),
							couponsRS.getDate("START_DATE"), couponsRS.getDate("END_DATE"), couponsRS.getInt("AMOUNT"),
							TYPE, couponsRS.getDouble("PRICE"));

					coupons.add(coupon);
				} catch (SQLException e) { // TODO: log warning/print to console
					handler.log("WARN: Could't get data for specific " + ARCHIVED + "Coupon owned by Customer.\n"
							+ e.getMessage());
				}
			}
			// throw new EmptyFilteredList(MessageType.ERR, ItemType.COUPON, tbl_coupon,
			// "TYPE="+type.toString());
			if (coupons.size() < 1)
				return null;
		} catch (SQLException e) {
			if (coupons.size() < 1)
				throw new UnexpectedError(MessageType.ERR, "Could't get data for " + errDetail, e.getMessage());
			else // TODO: log warning/print to console
				handler.log("WARN: Could't get data for " + errDetail + "\n" + e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for " + errDetail, e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for " + errDetail, e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for " + errDetail, e.getDetails());
		} finally {
			try {
				if (!keepStatementOpen)
					closeAllStatements();
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		return coupons;
	}

	/**
	 * Gets a list of a specific {@link Customer}'s {@link Coupon}s that fit at
	 * least one of the types and have a price lower than the specified price from
	 * the database, <em>either <strong>archived</strong>, or
	 * <strong>NON</strong>-archived</em>.
	 * 
	 * @param fromArchive
	 *            <em><u>true</u></em> for <strong>archived</strong> records, or
	 *            <em><u>false</u></em> for <strong>NON</strong>-archived</em>
	 *            records.
	 * @param types
	 *            the types
	 * @param maxPrice
	 *            the max price
	 * @param customer
	 *            the customer
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getCoupons(boolean fromArchive, CouponType[] types, double maxPrice, Customer customer)
			throws UnexpectedError {
		if (customer == null)
			throw new UnexpectedError(MessageType.ERR, "Can't get Coupon data for unspecified Customer",
					"Customer is null");

		final String ARCHIVED, KEY;
		ArrayList<Coupon> coupons = new ArrayList<>();
		ResultSet couponsRS = null;
		String selectCouponsQuery = "SELECT * FROM ";

		if (fromArchive) {
			selectCouponsQuery += archive_coupon + " JOIN (SELECT " + JOIN_COUPON_KEY + " FROM " + archive_join_customer
					+ " WHERE " + JOIN_CUSTOMER_KEY + " = " + customer.getId() + ") c ON " + archive_coupon + ".ID = c."
					+ JOIN_COUPON_KEY + "";
			ARCHIVED = "archived ";
			KEY = COUPON_KEY_ARCHIVE;
		} else {
			selectCouponsQuery += tbl_coupon + " JOIN (SELECT " + JOIN_COUPON_KEY + " FROM " + tbl_join_customer
					+ " WHERE " + JOIN_CUSTOMER_KEY + " = " + customer.getId() + ") c ON " + tbl_coupon + ".ID = c."
					+ JOIN_COUPON_KEY + "";
			ARCHIVED = "";
			KEY = COUPON_KEY;
		}
		selectCouponsQuery += " WHERE PRICE <= " + maxPrice;

		Object[] selectCouponsArgs = null;
		String errDetail = "max price filtered " + ARCHIVED + "Coupons";
		if (types != null) {
			if (types.length > 0) {
				selectCouponsArgs = new Object[types.length];
				errDetail = "max price and type filtered " + ARCHIVED + "Coupons";
				selectCouponsQuery += " AND (TYPE = ?";
				selectCouponsArgs[0] = types[0].toString();
				for (int i = 1; i < types.length; i++) {
					selectCouponsQuery += " OR TYPE = ?";
					selectCouponsArgs[i] = types[i].toString();
				}
				selectCouponsQuery += ")";
			}
		}
		System.out.println(selectCouponsQuery);
		errDetail += " owned by Customer.";

		Connection conn = null;

		try {
			conn = pool.getConnection();
			couponsRS = getResult(conn, selectCouponsQuery, selectCouponsArgs);

			while (couponsRS.next()) {
				try {
					Coupon coupon;
					String type = couponsRS.getString("TYPE");
					final CouponType TYPE = CouponType.getEnum(type);
					if (TYPE.isUnspecified() && !type.equalsIgnoreCase(CouponType.UNSPECIFIED.toString()))
						handler.log("NOTE: Unkown Coupon type.");

					coupon = new Coupon(couponsRS.getLong(KEY), couponsRS.getString("TITLE"),
							couponsRS.getString("MESSAGE"), couponsRS.getString("IMAGE"),
							couponsRS.getDate("START_DATE"), couponsRS.getDate("END_DATE"), couponsRS.getInt("AMOUNT"),
							TYPE, couponsRS.getDouble("PRICE"));

					coupons.add(coupon);
				} catch (SQLException e) { // TODO: log warning/print to console
					handler.log("WARN: Could't get data for specific " + ARCHIVED + "Coupon owned by Customer.\n"
							+ e.getMessage());
				}
			}
			// throw new EmptyFilteredList(MessageType.ERR, ItemType.COUPON, tbl_coupon,
			// "TYPE="+type.toString());
			if (coupons.size() < 1)
				return null;
		} catch (SQLException e) {
			if (coupons.size() < 1)
				throw new UnexpectedError(MessageType.ERR, "Could't get data for " + errDetail, e.getMessage());
			else // TODO: log warning/print to console
				handler.log("WARN: Could't get data for " + errDetail + "\n" + e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for " + errDetail, e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for " + errDetail, e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for " + errDetail, e.getDetails());
		} finally {
			try {
				if (!keepStatementOpen)
					closeAllStatements();
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		return coupons;
	}

	///////////////////////
	// OVERRIDES //
	///////////////////////

	@Override
	public boolean createCustomer(Customer customer) throws UnexpectedError {
		if (customer == null)
			throw new UnexpectedError(MessageType.ERR, "Can't create record for unspecified Customer",
					"Customer is null");
		Connection conn = null;
		String insertCustomerCouponQuery = "INSERT INTO " + tbl_join_customer + " (" + JOIN_CUSTOMER_KEY + ", "
				+ JOIN_COUPON_KEY + ") VALUES (?, ?)";
		String createCustomerQuery = "INSERT INTO " + tbl_customer + " (" + CUSTOMER_KEY + ", " + CUSTOMER_NAME + ", "
				+ CUSTOMER_PASSWORD + ") VALUES (?, ?, ?)";
		Object[] insertCustomerCouponArgs = new Object[] { customer.getId(), null };
		Object[] createCustomerArgs = new Object[] { customer.getId(), customer.getName(), customer.getPassword() };

		try {
			conn = pool.getConnection();
			runStatement(conn, createCustomerQuery, createCustomerArgs);
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't create new Customer.", e.getMessage());
		} catch (DuplicateKeyError e) {
			return false;
		} catch (DBStatementError e) {
			// TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't  create new Customer.",
					"Unexpected error creating Customer in database table [" + tbl_customer + "] ID: "
							+ customer.getId() + ".\n" + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Connection error creating Customer in database.",
					"Connection error creating Customer in database table [" + tbl_customer + "] ID: "
							+ customer.getId() + ".\n" + e.getDetails());
		} finally {
			try {
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		for (Coupon c : customer.getCoupons()) {
			insertCustomerCouponArgs[1] = c.getId();
			conn = null;
			try {
				conn = pool.getConnection();
				runStatement(conn, insertCustomerCouponQuery, insertCustomerCouponArgs);
			} catch (IllegalArgumentException e) {
				handler.log("ERR: Couldn't create new Customer-Coupon relationship.\n" + e.getMessage());
			} catch (ForeignKeyError e) { // TODO: log warning/print to console
				if (e.getKey().equals(FK_COUPON_ID))
					handler.log("WARN: Can't create Customer-Coupon relationship, Coupon does not exists.");
				else if (e.getKey().equals(FK_CUSTOMER_ID))
					handler.log("WARN: Can't create Customer-Coupon relationship, Customer does not exists.");
				else
					handler.log("WARN: Can't create Customer-Coupon relationship, UNEXPECTED ERROR.\n"
							+ e.getSqlErrorDetails());
			} catch (DBStatementError e) { // TODO: log warning/print to console.
				handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
			} catch (DBError e) { // TODO: log warning/print to console.
				handler.log("WARN: Can't create Customer-Coupon relationship, UNEXPECTED ERROR.\n"
						+ e.getSqlErrorDetails());
			} catch (ConnectionPoolError e) {
				handler.log("WARN: Can't create Customer-Coupon relationship, CONNECTION ERROR.\n" + e.getMessage());
			} finally {
				try {
					pool.returnConnection(conn);
				} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
					handler.log(e.getMessage());
				}
			}
		}
		return true;
	}

	@Override
	public boolean removeCustomer(Customer customer) throws UnexpectedError {
		if (customer == null)
			throw new UnexpectedError(MessageType.ERR, "Can't remove record for unspecified Customer",
					"Customer is null");

		Connection conn = null;
		String deleteCustomerQuery = "DELETE FROM " + tbl_customer + " WHERE " + CUSTOMER_KEY + " = "
				+ customer.getId();

		try { // ****** DELETE from CUSTOMER table;
			conn = pool.getConnection();
			if (runStatement(conn, deleteCustomerQuery) < 1)
				return false;
			// throw new CustomerNotFound(MessageType.ERR, tbl_customer, customer.getId());

		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't delete Customer.", e.getMessage());
		} catch (ForeignKeyError e) {
			if (e.getKey().equals(FK_COUPON_ID))
				throw new UnexpectedError(MessageType.ERR,
						"Couldn't delete Customer, there is a Customer-Coupon relationship.", e.getSqlErrorDetails());
			else
				throw new UnexpectedError(MessageType.ERR, "Couldn't delete Customer, UNEXPECTED ERROR.",
						e.getSqlErrorDetails());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't delete Customer.",
					"Unexpected error deleting Customer from database table [" + tbl_customer + "] ID: "
							+ customer.getId() + ".\n" + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Connection error deleting Customer in database.",
					"Connection error deleting Customer from database table [" + tbl_customer + "] ID: "
							+ customer.getId() + ".\n" + e.getDetails());
		} finally {
			try {
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}
		return true;
	}

	@Override
	public boolean updateCustomer(Customer customer) throws UnexpectedError {
		if (customer == null)
			throw new UnexpectedError(MessageType.ERR, "Can't update record for unspecified Customer",
					"Customer is null");

		Connection conn = null;
		String customerUpdateQuery = "UPDATE " + tbl_customer + " SET " + CUSTOMER_NAME + " = ?, " + CUSTOMER_PASSWORD
				+ " = ?  WHERE " + CUSTOMER_KEY + " = ?";
		Object[] customerUpdateArgs = new Object[] { customer.getName(), customer.getPassword(), customer.getId() };

		try {
			conn = pool.getConnection();
			if (runStatement(conn, customerUpdateQuery, customerUpdateArgs) < 1)
				return doesCustomerExist(customer);

		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't update Customer.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DuplicateKeyError e) {
			return false;
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't update Customer.", e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't update Customer.", e.getMessage());
		} finally {
			try {
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}
		return true;
	}

	@Override
	public Customer getCustomer(long id) throws UnexpectedError {
		Customer customer = null;
		ResultSet customerRS = null;
		String selectCustomerQuery = "SELECT * FROM " + tbl_customer + " WHERE " + CUSTOMER_KEY + " = " + id;
		Connection conn = null;

		try {
			conn = pool.getConnection();
			customerRS = getResult(conn, selectCustomerQuery);

			if (customerRS.next()) {
				customer = new Customer(customerRS.getString(CUSTOMER_NAME), customerRS.getString(CUSTOMER_PASSWORD),
						customerRS.getLong(CUSTOMER_KEY));
				ArrayList<Coupon> coupons = null;
				// ********************************** RUN OVER Customer COUPONS
				try {
					coupons = getCoupons(customer);
				} catch (UnexpectedError e) {
					handler.log(e.getMessage() + "\n" + e.getDetails());
				}
				if (coupons != null) {
					customer.setCoupons(coupons);
				}
			} else
				return customer;
			// throw new CustomerNotFound(MessageType.ERR, tbl_customer, id);

		} catch (SQLException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Customer.", e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Customer.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Customer.",
					e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Customer.", e.getDetails());
		} finally {
			try {
				closeAllStatements();
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		return customer;
	}

	@Override
	public Customer getCustomerOnly(long id) throws UnexpectedError {
		Customer customer = null;
		ResultSet customerRS = null;
		String selectCustomerQuery = "SELECT * FROM " + tbl_customer + " WHERE " + CUSTOMER_KEY + " = " + id;
		Connection conn = null;

		try {
			conn = pool.getConnection();
			customerRS = getResult(conn, selectCustomerQuery);

			if (customerRS.next()) {
				customer = new Customer(customerRS.getString(CUSTOMER_NAME), customerRS.getString(CUSTOMER_PASSWORD),
						customerRS.getLong(CUSTOMER_KEY));
			} else
				return customer;
		} catch (SQLException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Customer.", e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Customer.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Customer.",
					e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Customer.", e.getDetails());
		} finally {
			try {
				closeAllStatements();
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		return customer;
	}

	@Override
	public ArrayList<Customer> getAllCustomers() throws UnexpectedError {
		ArrayList<Customer> customers = new ArrayList<>();
		this.keepStatementOpen = true;
		ResultSet customersRS = null;
		String selectCustomersQuery = "SELECT * FROM " + tbl_customer;
		Connection conn = null;

		try {
			conn = pool.getConnection();
			customersRS = getResult(conn, selectCustomersQuery);

			while (customersRS.next()) {
				try {
					Customer customer = new Customer(customersRS.getString(CUSTOMER_NAME),
							customersRS.getString(CUSTOMER_PASSWORD), customersRS.getLong(CUSTOMER_KEY));
					ArrayList<Coupon> coupons = null;
					// ********************************** RUN OVER Customer COUPONS
					try {
						coupons = getCoupons(customer);
					} catch (UnexpectedError e) {
						handler.log(e.getMessage() + "\n" + e.getDetails());
					}
					if (coupons != null) {
						customer.setCoupons(coupons);
					}

					customers.add(customer);

				} catch (SQLException e) { // TODO: log warning/print to console
					handler.log("WARN: Could't get data for specific Customer in Customers query.\n" + e.getMessage());
				}
			}
			if (customers.size() < 1)
				return null;
			// throw new EmptyItemList(MessageType.ERR, ItemType.CUSTOMER, tbl_customer);

		} catch (SQLException e) {
			if (customers.size() < 1)
				throw new UnexpectedError(MessageType.ERR, "Could't get data for Customers query.", e.getMessage());
			else // TODO: log warning/print to console
				handler.log("WARN: Could't get data for all Customers query.\n" + e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for all Customers query.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for all Customers query.",
					e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for all Customers query.", e.getDetails());
		} finally {
			try {
				this.keepStatementOpen = false;
				closeAllStatements();
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		return customers;
	}

	@Override
	public ArrayList<Coupon> getCoupons(CouponType[] types, Customer customer) throws UnexpectedError {
		return getCoupons(false, types, customer);
	}

	@Override
	public ArrayList<Coupon> getCoupons(CouponType[] types, double maxPrice, Customer customer) throws UnexpectedError {
		return getCoupons(false, types, maxPrice, customer);
	}

	@Override
	public ArrayList<Coupon> getCoupons(double maxPrice, Customer customer) throws UnexpectedError {
		return getCoupons(null, maxPrice, customer);
	}

	@Override
	public ArrayList<Coupon> getCoupons(Customer customer) throws UnexpectedError {
		return getCoupons(null, customer);
	}

	@Override
	public ArrayList<Coupon> getCouponsByType(CouponType type, Customer customer) throws UnexpectedError {
		return getCoupons(new CouponType[] { type }, customer);
	}

	@Override
	public ArrayList<Coupon> getCouponsByType(CouponType type, double maxPrice, Customer customer)
			throws UnexpectedError {
		return getCoupons(new CouponType[] { type }, maxPrice, customer);
	}

	@Override
	public ArrayList<Coupon> getArchivedCoupons(CouponType[] types, Customer customer) throws UnexpectedError {
		return getCoupons(true, types, customer);
	}

	@Override
	public ArrayList<Coupon> getArchivedCoupons(CouponType[] types, double maxPrice, Customer customer)
			throws UnexpectedError {
		return getCoupons(true, types, maxPrice, customer);
	}

	@Override
	public ArrayList<Coupon> getArchivedCoupons(double maxPrice, Customer customer) throws UnexpectedError {
		return getArchivedCoupons(null, maxPrice, customer);
	}

	@Override
	public ArrayList<Coupon> getArchivedCoupons(Customer customer) throws UnexpectedError {
		return getArchivedCoupons(null, customer);
	}

	@Override
	public ArrayList<Coupon> getArchivedCouponsByType(CouponType type, Customer customer) throws UnexpectedError {
		return getArchivedCoupons(new CouponType[] { type }, customer);
	}

	@Override
	public ArrayList<Coupon> getArchivedCouponsByType(CouponType type, double maxPrice, Customer customer)
			throws UnexpectedError {
		return getArchivedCoupons(new CouponType[] { type }, maxPrice, customer);
	}

	@Override
	public Customer login(String custName, String password) throws UnexpectedError {
		if (custName == null || custName.equals("") || password == null || password.equals(""))
			throw new UnexpectedError(MessageType.ERR, "Can't request login for unspecified username or password",
					"Either username or password are empty/null");

		String loginQuery = "SELECT * FROM " + tbl_customer + " WHERE " + CUSTOMER_NAME + " = '"
				+ custName.toLowerCase() + "' AND PASSWORD = '" + password + "'";
		this.keepStatementOpen = true;
		ResultSet result = null;
		Customer customer = null;
		Connection conn = null;

		try {
			conn = pool.getConnection();
			if ((result = getResult(conn, loginQuery)).next()) {
				customer = new Customer(result.getString("" + CUSTOMER_NAME + ""), result.getString(CUSTOMER_PASSWORD),
						result.getLong(CUSTOMER_KEY));
				ArrayList<Coupon> coupons = null;
				// ********************************** RUN OVER Customer COUPONS
				try {
					coupons = getCoupons(customer);
				} catch (UnexpectedError e) {
					handler.log(e.getMessage() + "\n" + e.getDetails());
				}
				if (coupons != null) {
					customer.setCoupons(coupons);
				}
				return customer;
			}
		} catch (SQLException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't verify login information.111", e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't verify login information.222", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't verify login information.333", e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't send login information44.", e.getDetails());
		} finally {
			try {
				this.keepStatementOpen = false;
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}
		return null;
	}

	@Override
	public final boolean doesCustomerExist(Customer customer) throws UnexpectedError {
		if (customer == null)
			throw new UnexpectedError(MessageType.ERR, "Can't read record for unspecified Customer",
					"Customer is null");

		String doesCustomerExistQuery = "SELECT * FROM " + tbl_customer + " WHERE " + CUSTOMER_KEY + " = "
				+ customer.getId() + " LIMIT 1";
		Connection conn = null;

		try {
			conn = pool.getConnection();
			return getResult(conn, doesCustomerExistQuery).next();
		} catch (SQLException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't detect if Customer exists.", e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't detect if Customer exists.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't detect if Customer exists.", e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't detect if Customer exists.", e.getDetails());
		} finally {
			try {
				closeAllStatements();
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}
		return false;
	}
}
