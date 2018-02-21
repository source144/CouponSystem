package DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Bean.Company;
import Bean.Coupon;
import Bean.CouponType;
import Bean.Customer;
import DAO.CouponDAO;
import Exceptions.MessageType;
import Exceptions.DBError.DBError;
import Exceptions.DBError.DBStatementError;
import Exceptions.DBError.DuplicateKeyError;
import Exceptions.DBError.ForeignKeyError;
import Exceptions.General.ConnectionPoolError;
import Exceptions.General.UnexpectedError;

/**
 * Coupon MySQL DATABASE active data object.
 * 
 * @author Gonen Matias
 * @version 1.0 02/02/2018
 * 
 */
public class CouponDBDAO extends UtilDBDAO implements CouponDAO {

	/**
	 * Constructs the {@link CouponDBDAO}.
	 */
	public CouponDBDAO() {
		super();
	}

	///////////////////////
	// PRIVATE METHODS //
	///////////////////////

	/**
	 * Gets a list of all {@link Coupon}s that fit at least one of the types from
	 * the database, <em>either <strong>archived</strong>, or
	 * <strong>NON</strong>-archived</em>.
	 * 
	 * @param fromArchive
	 *            <em><u>true</u></em> for <strong>archived</strong> records, or
	 *            <em><u>false</u></em> for <strong>NON</strong>-archived</em>
	 *            records.
	 * @param types
	 *            the types
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	private final ArrayList<Coupon> getCoupons(boolean fromArchive, CouponType[] types) throws UnexpectedError {
		final String KEY, ARCHIVED;
		ArrayList<Coupon> coupons = new ArrayList<>();
		ResultSet couponsRS = null;
		String selectCouponsQuery = "SELECT * FROM ";

		if (fromArchive) {
			selectCouponsQuery += archive_coupon;
			ARCHIVED = "archived ";
			KEY = COUPON_KEY_ARCHIVE;
		} else {
			selectCouponsQuery += tbl_coupon;
			ARCHIVED = "";
			KEY = COUPON_KEY;
		}

		String errDetail = ARCHIVED + "Coupons";
		Object[] selectCouponsArgs = null;
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
		errDetail += " query.";

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
					handler.log("WARN: Could't get data for specific " + ARCHIVED + " Coupon in " + errDetail + "\n"
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
				closeAllStatements();
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		return coupons;
	}

	/**
	 * Gets a list of all {@link Coupon}s that fit at least one of the types and
	 * have a price lower than the specified price from the database, <em>either
	 * <strong>archived</strong>, or <strong>NON</strong>-archived</em>.
	 * 
	 * @param fromArchive
	 *            <em><u>true</u></em> for <strong>archived</strong> records, or
	 *            <em><u>false</u></em> for <strong>NON</strong>-archived</em>
	 *            records.
	 * @param types
	 *            the types
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public final ArrayList<Coupon> getCoupons(boolean fromArchive, CouponType[] types, double maxPrice)
			throws UnexpectedError {
		final String ARCHIVED, KEY;
		ArrayList<Coupon> coupons = new ArrayList<>();
		ResultSet couponsRS = null;
		String selectCouponsQuery = "SELECT * FROM ";

		if (fromArchive) {
			selectCouponsQuery += archive_coupon;
			ARCHIVED = "archived ";
			KEY = COUPON_KEY_ARCHIVE;
		} else {
			selectCouponsQuery += tbl_coupon;
			ARCHIVED = "";
			KEY = COUPON_KEY;
		}

		selectCouponsQuery += " WHERE PRICE <= " + maxPrice;

		String errDetail = "max price filtered " + ARCHIVED + "Coupons";
		Object[] selectCouponsArgs = null;
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
		errDetail += " query.";

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
					handler.log("WARN: Could't get data for specific " + ARCHIVED + "Coupon in " + errDetail + "\n"
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
				closeAllStatements();
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		return coupons;
	}

	/**
	 * Gets a {@link Coupon} from the database by id, <em>either
	 * <strong>archived</strong>, or <strong>NON</strong>-archived</em>.
	 * 
	 * @param fromArchive
	 *            <em><u>true</u></em> for <strong>archived</strong> records, or
	 *            <em><u>false</u></em> for <strong>NON</strong>-archived</em>
	 *            records.
	 * @param id
	 *            the id
	 * @return returns the {@link Coupon} or null if {@link Coupon} does not exist.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public final Coupon getCoupon(boolean fromArchive, long id) throws UnexpectedError {
		final String ARCHIVED, KEY;
		ResultSet couponRS = null;
		Connection conn = null;
		String selectCouponQuery = "SELECT * FROM ";

		if (fromArchive) {
			selectCouponQuery += archive_coupon;
			ARCHIVED = "archived ";
			KEY = COUPON_KEY_ARCHIVE;
		} else {
			selectCouponQuery += tbl_coupon;
			ARCHIVED = "";
			KEY = COUPON_KEY;
		}

		selectCouponQuery += " WHERE " + KEY + " = " + id;

		System.out.println(selectCouponQuery);
		try {
			conn = pool.getConnection();
			couponRS = getResult(conn, selectCouponQuery);

			if (couponRS.next()) {
				String type = couponRS.getString("TYPE");
				final CouponType TYPE = CouponType.getEnum(type);
				if (TYPE.isUnspecified() && !type.equalsIgnoreCase(CouponType.UNSPECIFIED.toString()))
					handler.log("NOTE: Unkown Coupon type.");

				return new Coupon(couponRS.getLong(KEY), couponRS.getString("TITLE"), couponRS.getString("MESSAGE"),
						couponRS.getString("IMAGE"), couponRS.getDate("START_DATE"), couponRS.getDate("END_DATE"),
						couponRS.getInt("AMOUNT"), TYPE, couponRS.getDouble("PRICE"));
			}
			// throw new CompanyNotFound(MessageType.ERR, tbl_company, id);

		} catch (SQLException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc " + ARCHIVED + "Coupon.",
					e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc " + ARCHIVED + "Coupon.",
					e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc " + ARCHIVED + "Coupon.",
					e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc " + ARCHIVED + "Coupon.",
					e.getDetails());
		} finally {
			try {
				closeAllStatements();
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}
		return null;
	}

	///////////////////////
	// OVERRIDES //
	///////////////////////

	@Override
	public final boolean doesCouponExist(Coupon coupon) throws UnexpectedError {
		if (coupon == null)
			throw new UnexpectedError(MessageType.ERR, "Can't read record for unspecified Coupon", "Coupon is null");

		String doesCouponExistQuery = "SELECT * FROM " + tbl_coupon + " WHERE " + COUPON_KEY + " = " + coupon.getId()
				+ " LIMIT 1";
		Connection conn = null;

		try {
			conn = pool.getConnection();
			return getResult(conn, doesCouponExistQuery).next();
		} catch (SQLException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't detect if Coupon exists.", e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't detect if Coupon exists.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't detect if Coupon exists.", e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't detect if Coupon exists.", e.getDetails());
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

	@Override
	public boolean createCoupon(Coupon coupon) throws UnexpectedError {
		Connection conn = null;
		boolean executedQuery = false;
		String createCouponQuery = "INSERT INTO " + tbl_coupon + " (" + COUPON_KEY + ", " + COUPON_TITLE + ", "
				+ COUPON_START + ", " + COUPON_END + ", " + COUPON_AMOUNT + ", " + COUPON_TYPE + ", " + COUPON_MESSAGE
				+ ", " + COUPON_PRICE + ", " + COUPON_IMAGE + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Object[] createCouponArgs = new Object[] { coupon.getId(), coupon.getTitle(), coupon.getStartDate(),
				coupon.getEndDate(), coupon.getAmount(), coupon.getType().toString(), coupon.getMessage(),
				coupon.getPrice(), coupon.getImage() };

		try {
			conn = pool.getConnection();
			executedQuery = (runStatement(conn, createCouponQuery, createCouponArgs) > -1);
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't create new Coupon.", e.getMessage());
		} catch (DuplicateKeyError e) {
			return false;
		} catch (DBStatementError e) {
			// TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't create new Coupon.",
					"Unexpected error creating Coupon in database table [" + tbl_coupon + "] ID: " + coupon.getId()
							+ ".\n" + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			if (executedQuery) // TODO: log warning/print to console.
				handler.log(e.getMessage());
			else
				throw new UnexpectedError(MessageType.ERR, "Connection error creating Coupon in database.",
						"Connection error creating Coupon in database table [" + tbl_coupon + "] ID: " + coupon.getId()
								+ ".\n" + e.getDetails());
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
	public boolean createCoupon(Coupon coupon, Company company) throws UnexpectedError {
		if (coupon == null)
			throw new UnexpectedError(MessageType.ERR, "Can't create record for unspecified Coupon", "Coupon is null");
		if (company == null)
			throw new UnexpectedError(MessageType.ERR, "Can't create record for unspecified Coupon", "Coupon is null");
		Connection conn = null;
		boolean executedQuery = false;
		String insertCompanyCouponQuery = "INSERT INTO " + tbl_join_company + " (" + JOIN_COMPANY_KEY + ", "
				+ JOIN_COUPON_KEY + ") VALUES (?, ?)";
		String createCouponQuery = "INSERT INTO " + tbl_coupon
				+ " (ID, TITLE, START_DATE, END_DATE, AMOUNT, TYPE, MESSAGE, PRICE, IMAGE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Object[] insertCompanyCouponArgs = new Object[] { company.getId(), coupon.getId() };
		Object[] createCouponArgs = new Object[] { coupon.getId(), coupon.getTitle(), coupon.getStartDate(),
				coupon.getEndDate(), coupon.getAmount(), coupon.getType().toString(), coupon.getMessage(),
				coupon.getPrice(), coupon.getImage() };

		try {
			conn = pool.getConnection();
			executedQuery = (runStatement(conn, createCouponQuery, createCouponArgs) > -1);
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't create new Coupon.", e.getMessage());
		} catch (DuplicateKeyError e) {
			return false;
		} catch (DBStatementError e) {
			// TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't create new Coupon.",
					"Unexpected error creating Coupon in database table [" + tbl_coupon + "] ID: " + coupon.getId()
							+ ".\n" + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			if (executedQuery) // TODO: log warning/print to console.
				handler.log(e.getMessage());
			else
				throw new UnexpectedError(MessageType.ERR, "Connection error creating Coupon in database.",
						"Connection error creating Coupon in database table [" + tbl_coupon + "] ID: " + coupon.getId()
								+ ".\n" + e.getDetails());
		} finally {
			try {
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		boolean joinTableSuccess = false;
		conn = null;
		try {
			conn = pool.getConnection();
			joinTableSuccess = runStatement(conn, insertCompanyCouponQuery, insertCompanyCouponArgs) > 0;
		} catch (IllegalArgumentException e) {
			handler.log("ERR: Couldn't create new Company-Coupon relationship.\n" + e.getMessage());
		} catch (ForeignKeyError e) { // TODO: log warning/print to console
			if (e.getKey().equals(FK_COUPON_ID))
				handler.log("WARN: Can't create Company-Coupon relationship, Coupon does not exists.");
			else if (e.getKey().equals(FK_COMPANY_ID))
				handler.log("WARN: Can't create Company-Coupon relationship, Company does not exists.");
			else
				handler.log(
						"WARN: Can't create Company-Coupon relationship, UNEXPECTED ERROR.\n" + e.getSqlErrorDetails());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) { // TODO: log warning/print to console.
			handler.log("WARN: Can't create Company-Coupon relationship, UNEXPECTED ERROR.\n" + e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			if (joinTableSuccess) // TODO: log warning/print to console
				handler.log(e.getMessage());
			else
				handler.log("WARN: Can't create Company-Coupon relationship, CONNECTION ERROR.\n" + e.getMessage());
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
	public boolean removeCoupon(Coupon coupon) throws UnexpectedError {
		if (coupon == null)
			throw new UnexpectedError(MessageType.ERR, "Can't remove record for unspecified Coupon", "Coupon is null");

		Connection conn = null;
		String deleteCouponQuery = "DELETE FROM " + tbl_coupon + " WHERE ID = " + coupon.getId();

		try { // ****** DELETE from COUPON table ****************************
			conn = pool.getConnection();
			if (runStatement(conn, deleteCouponQuery) < 1)
				return false;
		} catch (IllegalArgumentException e) {
			handler.log("ERR: Couldn't delete Coupon.\n" + e.getMessage());
		} catch (ForeignKeyError e) { // TODO: log warning/print to console
			if (e.getKey().equals(FK_CUSTOMER_ID))
				handler.log("WARN: Couldn't delete Coupon, a Customer has this Coupon.");
			else if (e.getKey().equals(FK_COMPANY_ID))
				handler.log("WARN: Couldn't delete Coupon, a Company owns this Coupon.");
			else
				handler.log("WARN: Couldn't delete Coupon, UNEXPECTED ERROR.\n" + e.getSqlErrorDetails());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			handler.log("WARN: Couldn't delete Coupon.\n" + e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			handler.log("WARN: Couldn't delete Coupon.\n" + e.getDetails());
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
	public boolean updateCoupon(Coupon coupon) throws UnexpectedError {
		if (coupon == null)
			throw new UnexpectedError(MessageType.ERR, "Can't update record for unspecified Coupon", "Coupon is null");

		Connection conn = null;
		String couponUpdateQuery = "UPDATE " + tbl_coupon + " SET " + COUPON_TITLE + " = ?, " + COUPON_START + " = ?, "
				+ COUPON_END + " = ?, " + COUPON_AMOUNT + " = ?, " + COUPON_TYPE + " = ?, " + COUPON_MESSAGE + " = ?, "
				+ COUPON_PRICE + " = ?, " + COUPON_IMAGE + " = ?" + " WHERE " + COUPON_KEY + " = ?";
		Object[] couponUpdateArgs = new Object[] { coupon.getTitle(), coupon.getStartDate(), coupon.getEndDate(),
				coupon.getAmount(), coupon.getType().toString(), coupon.getMessage(), coupon.getPrice(),
				coupon.getImage(), coupon.getId() };

		try {
			conn = pool.getConnection();
			if (runStatement(conn, couponUpdateQuery, couponUpdateArgs) < 1)
				return doesCouponExist(coupon);

		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't update Coupon.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DuplicateKeyError e) {
			return false;
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't update Coupon.", e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't update Coupon.", e.getMessage());
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
	public Coupon getCoupon(long id) throws UnexpectedError {
		return getCoupon(false, id);
	}

	@Override
	public ArrayList<Coupon> getCoupons(CouponType[] types) throws UnexpectedError {
		return getCoupons(false, types);

	}

	@Override
	public ArrayList<Coupon> getCoupons(CouponType[] types, double maxPrice) throws UnexpectedError {
		return getCoupons(false, types, maxPrice);
	}

	@Override
	public ArrayList<Coupon> getCoupons(double maxPrice) throws UnexpectedError {
		return getCoupons(null, maxPrice);
	}

	@Override
	public ArrayList<Coupon> getCouponsByType(CouponType type) throws UnexpectedError {
		return getCoupons(new CouponType[] { type });
	}

	@Override
	public Coupon getArchivedCoupon(long id) throws UnexpectedError {
		return getCoupon(true, id);
	}

	@Override
	public ArrayList<Coupon> getArchivedCoupons(CouponType[] types) throws UnexpectedError {
		return getCoupons(true, types);

	}

	@Override
	public ArrayList<Coupon> getArchivedCoupons(CouponType[] types, double maxPrice) throws UnexpectedError {
		return getCoupons(true, types, maxPrice);
	}

	@Override
	public ArrayList<Coupon> getArchivedCoupons(double maxPrice) throws UnexpectedError {
		return getArchivedCoupons(null, maxPrice);
	}

	@Override
	public ArrayList<Coupon> getArchivedCouponsByType(CouponType type) throws UnexpectedError {
		return getArchivedCoupons(new CouponType[] { type });
	}

	@Override
	public ArrayList<Coupon> getAllCoupons() throws UnexpectedError {
		return getCoupons(null);
	}

	@Override
	public ArrayList<Coupon> getAllArchivedCoupons() throws UnexpectedError {
		return getArchivedCoupons(null);
	}

	///////////////////////
	// JOIN METHODS //
	///////////////////////

	@Override
	public boolean addCouponToArchive(Coupon coupon) throws UnexpectedError {
		if (coupon == null)
			throw new UnexpectedError(MessageType.ERR, "Can't archive unspecified Coupon", "Coupon is null");
		// Does coupon exist?
		try {
			if (!doesCouponExist(coupon))
				return false; // false - return false;
		} catch (UnexpectedError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't archive Coupon.", e.getDetails());
		}
		// true continue to archive

		String createArchiveCouponQuery = "INSERT INTO " + archive_coupon
				+ " (OLD_ID, TITLE, START_DATE, END_DATE, AMOUNT, TYPE, MESSAGE, PRICE, IMAGE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Object[] createArchiveCouponArgs = new Object[] { coupon.getId(), coupon.getTitle(), coupon.getStartDate(),
				coupon.getEndDate(), coupon.getAmount(), coupon.getType().toString(), coupon.getMessage(),
				coupon.getPrice(), coupon.getImage() };

		Connection conn = null;
		long generatedId = -1;

		// create coupon in archive table and getId of the archived row;
		try {
			conn = pool.getConnection();
			generatedId = insertAndGetLongKey(conn, createArchiveCouponQuery, createArchiveCouponArgs);
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't create new Coupon archive record.", e.getMessage());
		} catch (DuplicateKeyError e) {
			return false;
		} catch (DBStatementError e) {
			// TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't create new Coupon archive record.",
					"Unexpected error creating Coupon archive record in database table [" + archive_coupon + "] ID: "
							+ coupon.getId() + ".\n" + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			if (generatedId != -1) // TODO: log warning/print to console.
				handler.log(e.getMessage());
			else
				throw new UnexpectedError(MessageType.ERR, "Connection error creating Coupon archive record.",
						"Connection error creating Coupon archive record in database table [" + archive_coupon
								+ "] ID: " + coupon.getId() + ".\n" + e.getDetails());
		} finally {
			try {
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		Object[] insertJoinTableArgs = new Object[] { null, generatedId };
		ResultSet tempResultSet = null;

		// create relationship with customer in archive-customer-coupon table;
		try {
			conn = pool.getConnection();
			String selectCustomerCouponQuery = "SELECT " + JOIN_CUSTOMER_KEY + " FROM " + tbl_join_customer + " WHERE "
					+ JOIN_COUPON_KEY + " = " + coupon.getId();
			tempResultSet = getResult(conn, selectCustomerCouponQuery);

			while (tempResultSet.next()) { // for every relationship
				try {

					insertJoinTableArgs[0] = tempResultSet.getLong(1);
					String insertCustomerCouponQuery = "INSERT INTO " + archive_join_customer + " (" + JOIN_CUSTOMER_KEY
							+ ", " + JOIN_COUPON_KEY + ") VALUES (?, ?)";
					runStatement(conn, insertCustomerCouponQuery, insertJoinTableArgs);

				} catch (SQLException e) {
					handler.log(
							"WARN: Could't get data for a specific Customer-Coupon relationship.\n" + e.getMessage());
				} catch (IllegalArgumentException e) {
					handler.log("ERR: Couldn't create new Customer-Coupon record in archive.\n" + e.getMessage());
				} catch (ForeignKeyError e) { // TODO: log warning/print to console
					if (e.getKey().equals(FK_COUPON_ID))
						handler.log("WARN: Can't create Customer-Coupon record in archive, Coupon does not exists.");
					else if (e.getKey().equals(FK_COMPANY_ID))
						handler.log("WARN: Can't create Customer-Coupon record in archive, Company does not exists.");
					else
						handler.log("WARN: Can't create Customer-Coupon record in archive, UNEXPECTED ERROR.\n"
								+ e.getSqlErrorDetails());
				} catch (DBStatementError e) { // TODO: log warning/print to console.
					handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
				} catch (DBError e) { // TODO: log warning/print to console.
					handler.log("WARN: Can't create Customer-Coupon record in archive, UNEXPECTED ERROR.\n"
							+ e.getSqlErrorDetails());
				}
			}

		} catch (SQLException e) {
			handler.log("WARN: Could't get data for Customer-Coupon relationship.\n" + e.getMessage());
		} catch (IllegalArgumentException e) {
			handler.log("ERR: Couldn't get Customer-Coupon records.\n" + e.getMessage());
		} catch (ForeignKeyError e) { // TODO: log warning/print to console
			if (e.getKey().equals(FK_COUPON_ID))
				handler.log("WARN: Couldn't get Customer-Coupon records, Coupon does not exists.");
			else if (e.getKey().equals(FK_COMPANY_ID))
				handler.log("WARN: Couldn't get Customer-Coupon records, Company does not exists.");
			else
				handler.log("WARN: Couldn't get Customer-Coupon records, UNEXPECTED ERROR.\n" + e.getSqlErrorDetails());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) { // TODO: log warning/print to console.
			handler.log("WARN: Couldn't get Customer-Coupon records, UNEXPECTED ERROR.\n" + e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			handler.log("WARN: Couldn't get Customer-Coupon records, CONNECTION ERROR.\n" + e.getMessage());
		} finally {
			try {
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		// create relationship with company in archive-company-coupon table;
		conn = null;
		try {
			conn = pool.getConnection();
			String selectCompanyCouponsQuery = "SELECT " + JOIN_COMPANY_KEY + " FROM " + tbl_join_company + " WHERE "
					+ JOIN_COUPON_KEY + " = " + coupon.getId();
			tempResultSet = getResult(conn, selectCompanyCouponsQuery);

			while (tempResultSet.next()) { // for every relationship

				try {
					insertJoinTableArgs[0] = tempResultSet.getLong(1);
					String insertCompanyCouponQuery = "INSERT INTO " + archive_join_company + " (" + JOIN_COMPANY_KEY
							+ ", " + JOIN_COUPON_KEY + ") VALUES (?, ?)";
					runStatement(conn, insertCompanyCouponQuery, insertJoinTableArgs);
				} catch (SQLException e) {
					handler.log(
							"WARN: Could't get data for a specific Company-Coupon relationship.\n" + e.getMessage());
				} catch (IllegalArgumentException e) {
					handler.log("ERR: Couldn't create new Company-Coupon record in archive.\n" + e.getMessage());
				} catch (ForeignKeyError e) { // TODO: log warning/print to console
					if (e.getKey().equals(FK_COUPON_ID))
						handler.log("WARN: Can't create Company-Coupon record in archive, Coupon does not exists.");
					else if (e.getKey().equals(FK_COMPANY_ID))
						handler.log("WARN: Can't create Company-Coupon record in archive, Company does not exists.");
					else
						handler.log("WARN: Can't create Company-Coupon record in archive, UNEXPECTED ERROR.\n"
								+ e.getSqlErrorDetails());
				} catch (DBStatementError e) { // TODO: log warning/print to console.
					handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
				} catch (DBError e) { // TODO: log warning/print to console.
					handler.log("WARN: Can't create Company-Coupon record in archive, UNEXPECTED ERROR.\n"
							+ e.getSqlErrorDetails());
				}
			}

		} catch (SQLException e) {
			handler.log("WARN: Could't get data for Company-Coupon relationship.\n" + e.getMessage());
		} catch (IllegalArgumentException e) {
			handler.log("ERR: Couldn't get Company-Coupon records.\n" + e.getMessage());
		} catch (ForeignKeyError e) { // TODO: log warning/print to console
			if (e.getKey().equals(FK_COUPON_ID))
				handler.log("WARN: Couldn't get Company-Coupon records, Coupon does not exists.");
			else if (e.getKey().equals(FK_COMPANY_ID))
				handler.log("WARN: Couldn't get Company-Coupon records, Company does not exists.");
			else
				handler.log("WARN: Couldn't get Company-Coupon records, UNEXPECTED ERROR.\n" + e.getSqlErrorDetails());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) { // TODO: log warning/print to console.
			handler.log("WARN: Couldn't get Company-Coupon records, UNEXPECTED ERROR.\n" + e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			handler.log("WARN: Couldn't get Company-Coupon records, CONNECTION ERROR.\n" + e.getMessage());
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
	public boolean addCouponToCompany(Coupon coupon, Company company) throws UnexpectedError {
		if (company == null)
			throw new UnexpectedError(MessageType.ERR, "Can't create connection record for unspecified Company",
					"Company is null");
		if (coupon == null)
			throw new UnexpectedError(MessageType.ERR, "Can't create connection record for unspecified Coupon",
					"Coupon is null");
		Connection conn = null;
		String insertCompanyCouponQuery = "INSERT INTO " + tbl_join_company + " (" + JOIN_COMPANY_KEY + ", "
				+ JOIN_COUPON_KEY + ") VALUES (?, ?)";
		Object[] insertCompanyCouponArgs = new Object[] { company.getId(), coupon.getId() };
		boolean addedCompanyCoupon = false;
		try {
			conn = pool.getConnection();
			addedCompanyCoupon = (runStatement(conn, insertCompanyCouponQuery, insertCompanyCouponArgs) > 0);
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't create new company-coupon relationship.",
					e.getMessage());
		} catch (DuplicateKeyError e) {
			return false;
		} catch (ForeignKeyError e) { // TODO: log warning/print to console
			if (e.getKey().equals(FK_COMPANY_ID))
				return false;
			else if (e.getKey().equals(FK_COUPON_ID))
				return false;
			else {
				throw new UnexpectedError(MessageType.ERR,
						"Can't create Company-Coupon relationship, UNEXPECTED ERROR.", e.getSqlErrorDetails());
			}
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) { // TODO: log warning/print to console.
			throw new UnexpectedError(MessageType.ERR, "Couldn't create Company-Coupon relationship, UNEXPECTED ERROR.",
					e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			if (addedCompanyCoupon) // TODO: log warning/print to console.
				handler.log(e.getMessage());
			else
				throw new UnexpectedError(MessageType.ERR,
						"Can't create Company-Coupon relationship, CONNECTION ERROR.", e.getMessage());
		} finally {
			try {
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}
		return addedCompanyCoupon;
	}

	@Override
	public boolean addCouponToCustomer(Coupon coupon, Customer customer) throws UnexpectedError {
		if (customer == null)
			throw new UnexpectedError(MessageType.ERR, "Can't create connection record for unspecified Customer",
					"Customer is null");
		if (coupon == null)
			throw new UnexpectedError(MessageType.ERR, "Can't create connection record for unspecified Coupon",
					"Coupon is null");

		Connection conn = null;
		String insertCustomerCouponQuery = "INSERT INTO " + tbl_join_customer + " (" + JOIN_CUSTOMER_KEY + ", "
				+ JOIN_COUPON_KEY + ") VALUES (?, ?)";
		Object[] insertCustomerCouponArgs = new Object[] { customer.getId(), coupon.getId() };
		boolean addedCustomerCoupon = false;
		try {
			conn = pool.getConnection();
			addedCustomerCoupon = (runStatement(conn, insertCustomerCouponQuery, insertCustomerCouponArgs) > 0);
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't create new company-coupon relationship.66666",
					e.getMessage());
		} catch (DuplicateKeyError e) {
			return false;
		} catch (ForeignKeyError e) { // TODO: log warning/print to console
			if (e.getKey().equals(FK_CUSTOMER_ID))
				return false;
			else if (e.getKey().equals(FK_COUPON_ID))
				return false;
			else {
				throw new UnexpectedError(MessageType.ERR,
						"Can't create Customer-Coupon relationship, UNEXPECTED ERROR.", e.getSqlErrorDetails());
			}
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) { // TODO: log warning/print to console.
			throw new UnexpectedError(MessageType.ERR,
					"Couldn't create Customer-Coupon relationship, UNEXPECTED ERROR.", e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			if (addedCustomerCoupon) // TODO: log warning/print to console.
				handler.log(e.getMessage());
			else
				throw new UnexpectedError(MessageType.ERR,
						"Can't create Customer-Coupon relationship, CONNECTION ERROR.", e.getMessage());
		} finally {
			try {
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}
		return addedCustomerCoupon;
	}

	@Override
	public boolean removeCouponFromCompany(Coupon coupon, Company company) throws UnexpectedError {
		if (company == null)
			throw new UnexpectedError(MessageType.ERR, "Can't remove connection record for unspecified Company",
					"Company is null");
		if (coupon == null)
			throw new UnexpectedError(MessageType.ERR, "Can't remove connection record for unspecified Coupon",
					"Coupon is null");

		Connection conn = null;
		String deleteCompanyCouponQuery = "DELETE FROM " + tbl_join_company + " WHERE " + JOIN_COMPANY_KEY + " = ? AND "
				+ JOIN_COUPON_KEY + " = ?";
		Object[] deleteCompanyCouponArgs = new Object[] { company.getId(), coupon.getId() };
		boolean deletedCompanyCoupon = false;
		try {
			conn = pool.getConnection();
			deletedCompanyCoupon = (runStatement(conn, deleteCompanyCouponQuery, deleteCompanyCouponArgs) > 0);
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't delete Company-Coupon relationship.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) { // TODO: log warning/print to console.
			throw new UnexpectedError(MessageType.ERR, "Couldn't delete Company-Coupon relationship, UNEXPECTED ERROR.",
					e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			if (deletedCompanyCoupon) // TODO: log warning/print to console.
				handler.log(e.getMessage());
			else
				throw new UnexpectedError(MessageType.ERR,
						"Can't delete Company-Coupon relationship, CONNECTION ERROR.", e.getMessage());
		} finally {
			try {
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}
		return deletedCompanyCoupon;
	}

	@Override
	public boolean removeCouponFromCustomer(Coupon coupon, Customer customer) throws UnexpectedError {
		if (customer == null)
			throw new UnexpectedError(MessageType.ERR, "Can't remove connection record for unspecified Customer",
					"Customer is null");
		if (coupon == null)
			throw new UnexpectedError(MessageType.ERR, "Can't remove connection record for unspecified Coupon",
					"Coupon is null");

		Connection conn = null;
		String deleteCustomerCouponQuery = "DELETE FROM " + tbl_join_customer + " WHERE " + JOIN_CUSTOMER_KEY
				+ " = ? AND " + JOIN_COUPON_KEY + " = ?";
		Object[] deleteCustomerCouponArgs = new Object[] { customer.getId(), coupon.getId() };
		boolean deletedCustomerCoupon = false;
		try {
			conn = pool.getConnection();
			deletedCustomerCoupon = (runStatement(conn, deleteCustomerCouponQuery, deleteCustomerCouponArgs) > 0);
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't delete Customer-Coupon relationship.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) { // TODO: log warning/print to console.
			throw new UnexpectedError(MessageType.ERR,
					"Couldn't delete Customer-Coupon relationship, UNEXPECTED ERROR.", e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			if (deletedCustomerCoupon) // TODO: log warning/print to console.
				handler.log(e.getMessage());
			else
				throw new UnexpectedError(MessageType.ERR,
						"Can't delete Customer-Coupon relationship, CONNECTION ERROR.", e.getMessage());
		} finally {
			try {
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}
		return deletedCustomerCoupon;
	}

	@Override
	public boolean companyOwnsCoupon(Coupon coupon, Company company) throws UnexpectedError {
		if (company == null)
			throw new UnexpectedError(MessageType.ERR, "Can't read record for unspecified Company", "Company is null");
		if (coupon == null)
			throw new UnexpectedError(MessageType.ERR, "Can't read record for unspecified Coupon", "Coupon is null");

		Connection conn = null;
		String doesCompanyOwnCouponQuery = "SELECT * FROM " + tbl_join_company + " WHERE " + JOIN_COMPANY_KEY
				+ " = ? AND " + JOIN_COUPON_KEY + " = ?";
		Object[] doesCompanyOwnCouponArgs = new Object[] { company.getId(), coupon.getId() };
		boolean doesCompanyOwnCoupon = false;

		try {
			conn = pool.getConnection();
			doesCompanyOwnCoupon = getResult(conn, doesCompanyOwnCouponQuery, doesCompanyOwnCouponArgs).next();
		} catch (SQLException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Company-Coupon.", e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Company-Coupon.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Company-Coupon.",
					e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Company-Coupon.", e.getDetails());
		} finally {
			try {
				closeAllStatements();
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}
		return doesCompanyOwnCoupon;
	}

	@Override
	public int couponsLeft(Coupon coupon) throws UnexpectedError {
		if (coupon == null)
			throw new UnexpectedError(MessageType.ERR, "Can't read record for unspecified Coupon", "Coupon is null");
		Connection conn = null;
		String couponsOwnedQuery = "SELECT * FROM " + tbl_join_company + " WHERE " + JOIN_COUPON_KEY + " = ?";
		Object[] couponsOwnedArgs = new Object[] { coupon.getId() };
		int couponsLeft = 0;

		try {
			conn = pool.getConnection();
			couponsLeft = getResult(conn, couponsOwnedQuery, couponsOwnedArgs).getMetaData().getColumnCount();
		} catch (SQLException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get count for owned Coupons.", e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get count for owned Coupons.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get count for owned Coupons.", e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get count for owned Coupons.", e.getDetails());
		} finally {
			try {
				closeAllStatements();
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}
		return couponsLeft;
	}
}
