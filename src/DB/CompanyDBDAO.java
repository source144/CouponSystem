package DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Bean.Company;
import Bean.Coupon;
import Bean.CouponType;
import DAO.CompanyDAO;
import Exceptions.MessageType;
import Exceptions.DBError.DBError;
import Exceptions.DBError.DBStatementError;
import Exceptions.DBError.DuplicateKeyError;
import Exceptions.DBError.ForeignKeyError;
import Exceptions.General.ConnectionPoolError;
import Exceptions.General.UnexpectedError;

/**
 * Company MySQL DATABASE active data object.
 * 
 * @author Gonen Matias
 * @version 1.0 02/02/2018
 * 
 */
public class CompanyDBDAO extends UtilDBDAO implements CompanyDAO {
	/**
	 * For methods that require statements to stay open and call other methods that
	 * close them.
	 */
	private boolean keepStatementOpen = false;

	/**
	 * Constructs the {@link CompanyDBDAO}.
	 */
	public CompanyDBDAO() {
		super();
	}

	///////////////////////
	// PRIVATE METHODS //
	///////////////////////

	/**
	 * Gets a list of a specific {@link Company}'s {@link Coupon}s that fit at least
	 * one of the types from the database, <em>either <strong>archived</strong>, or
	 * <strong>NON</strong>-archived</em>.
	 * 
	 * @param fromArchive
	 *            <em><u>true</u></em> for <strong>archived</strong> records, or
	 *            <em><u>false</u></em> for <strong>NON</strong>-archived</em>
	 *            records.
	 * @param types
	 *            the types
	 * @param company
	 *            the company
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	private final ArrayList<Coupon> getCoupons(boolean fromArchive, CouponType[] types, Company company)
			throws UnexpectedError {
		if (company == null)
			throw new UnexpectedError(MessageType.ERR, "Can't get Coupon data for unspecified Company",
					"Company is null");

		final String ARCHIVED, KEY;
		ArrayList<Coupon> coupons = new ArrayList<>();
		ResultSet couponsRS = null;
		String selectCouponsQuery = "SELECT * FROM ";

		if (fromArchive) {
			selectCouponsQuery += archive_coupon + " JOIN (SELECT " + JOIN_COUPON_KEY + " FROM " + archive_join_company
					+ " WHERE " + JOIN_COMPANY_KEY + " = " + company.getId() + ") c WHERE " + archive_coupon
					+ ".ID = c." + JOIN_COUPON_KEY + "";
			ARCHIVED = "archived ";
			KEY = COUPON_KEY_ARCHIVE;
		} else {
			selectCouponsQuery += tbl_coupon + " JOIN (SELECT " + JOIN_COUPON_KEY + " FROM " + tbl_join_company
					+ " WHERE " + JOIN_COMPANY_KEY + " = " + company.getId() + ") c WHERE " + tbl_coupon + ".ID = c."
					+ JOIN_COUPON_KEY + "";
			ARCHIVED = "";
			KEY = COUPON_KEY;
		}

		Object[] selectCouponsArgs = null;
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
		errDetail += " owned by Company.";
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
					handler.log("WARN: Could't get data for specific " + ARCHIVED + "Coupon owned by Company.\n"
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
	 * Gets a list of a specific {@link Company}'s {@link Coupon}s that fit at least
	 * one of the types and have a price lower than the specified price from the
	 * database, <em>either <strong>archived</strong>, or
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
	 * @param company
	 *            the company
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	private final ArrayList<Coupon> getCoupons(boolean fromArchive, CouponType[] types, double maxPrice,
			Company company) throws UnexpectedError {
		if (company == null)
			throw new UnexpectedError(MessageType.ERR, "Can't get Coupon data for unspecified Company",
					"Company is null");

		final String ARCHIVED, KEY;
		ArrayList<Coupon> coupons = new ArrayList<>();
		ResultSet couponsRS = null;
		String selectCouponsQuery = "SELECT * FROM ";

		if (fromArchive) {
			selectCouponsQuery += archive_coupon + " JOIN (SELECT " + JOIN_COUPON_KEY + " FROM " + archive_join_company
					+ " WHERE " + JOIN_COMPANY_KEY + " = " + company.getId() + ") c WHERE " + archive_coupon
					+ ".ID = c." + JOIN_COUPON_KEY + "";
			ARCHIVED = "archived ";
			KEY = COUPON_KEY_ARCHIVE;
		} else {
			selectCouponsQuery += tbl_coupon + " JOIN (SELECT " + JOIN_COUPON_KEY + " FROM " + tbl_join_company
					+ " WHERE " + JOIN_COMPANY_KEY + " = " + company.getId() + ") c WHERE " + tbl_coupon + ".ID = c."
					+ JOIN_COUPON_KEY + "";
			ARCHIVED = "";
			KEY = COUPON_KEY;
		}

		Object[] selectCouponsArgs = null;
		selectCouponsQuery += " WHERE PRICE <= " + maxPrice;
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
		errDetail += " owned by Company.";
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
					handler.log("WARN: Could't get data for specific " + ARCHIVED + "Coupon owned by Company.\n"
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
	public boolean createCompany(Company company) throws UnexpectedError {
		if (company == null)
			throw new UnexpectedError(MessageType.ERR, "Can't create record for unspecified Company",
					"Company is null");

		Connection conn = null;
		String insertCompanyCouponQuery = "INSERT INTO " + tbl_join_company + " (" + JOIN_COMPANY_KEY + ", "
				+ JOIN_COUPON_KEY + ") VALUES (?, ?)";
		String createCompanyQuery = "INSERT INTO " + tbl_company + " (" + COMPANY_KEY + ", " + COMPANY_NAME + ", "
				+ COMPANY_PASSWORD + ", " + COMPANY_EMAIL + ") VALUES (?, ?, ?, ?)";
		Object[] insertCompanyCouponArgs = new Object[] { company.getId(), null };
		Object[] createCompanyArgs = new Object[] { company.getId(), company.getName(), company.getPassword(),
				company.getEmail() };

		try {
			conn = pool.getConnection();
			runStatement(conn, createCompanyQuery, createCompanyArgs);
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't create new Company.", e.getMessage());
		} catch (DuplicateKeyError e) {
			return false;
		} catch (DBStatementError e) {
			// TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't  create new Company.",
					"Unexpected error creating Company in database table [" + tbl_company + "] ID: " + company.getId()
							+ ".\n" + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Connection error creating Company in database.",
					"Connection error creating Company in database table [" + tbl_company + "] ID: " + company.getId()
							+ ".\n" + e.getDetails());
		} finally {
			try {
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		for (Coupon c : company.getCoupons()) {
			insertCompanyCouponArgs[1] = c.getId();
			conn = null;
			try {
				conn = pool.getConnection();
				runStatement(conn, insertCompanyCouponQuery, insertCompanyCouponArgs);
			} catch (IllegalArgumentException e) {
				handler.log("ERR: Couldn't create new Company-Coupon relationship.\n" + e.getMessage());
			} catch (ForeignKeyError e) { // TODO: log warning/print to console
				if (e.getKey().equals(FK_COUPON_ID))
					handler.log("WARN: Can't create Company-Coupon relationship, Coupon does not exists.");
				else if (e.getKey().equals(FK_COMPANY_ID))
					handler.log("WARN: Can't create Company-Coupon relationship, Company does not exists.");
				else
					handler.log("WARN: Can't create Company-Coupon relationship, UNEXPECTED ERROR.\n"
							+ e.getSqlErrorDetails());
			} catch (DBStatementError e) { // TODO: log warning/print to console.
				handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
			} catch (DBError e) { // TODO: log warning/print to console.
				handler.log(
						"WARN: Can't create Company-Coupon relationship, UNEXPECTED ERROR.\n" + e.getSqlErrorDetails());
			} catch (ConnectionPoolError e) {
				handler.log("WARN: Can't create Company-Coupon relationship, CONNECTION ERROR.\n" + e.getMessage());
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
	public boolean removeCompany(Company company) throws UnexpectedError {
		if (company == null)
			throw new UnexpectedError(MessageType.ERR, "Can't remove record for unspecified Company",
					"Company is null");

		Connection conn = null;
		ArrayList<Coupon> coupons = null;
		String deleteCompanyQuery = "DELETE FROM " + tbl_company + " WHERE " + COMPANY_KEY + " = " + company.getId();

		try { // ****** GET COMPANY'S COUPONS
			coupons = getCoupons(company);
		} catch (UnexpectedError e) {
			handler.log("WARN: Couldn't get Coupons owned by Company,\nSKIPPING ALL COUPON DELETE\n" + e.getDetails());
		}

		try { // ****** DELETE from COMPANY table;
			conn = pool.getConnection();
			if (runStatement(conn, deleteCompanyQuery) < 1)
				return false;
			// throw new CompanyNotFound(MessageType.ERR, tbl_company, company.getId());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't delete Company.", e.getMessage());
		} catch (ForeignKeyError e) {
			if (e.getKey().equals(FK_COUPON_ID))
				throw new UnexpectedError(MessageType.ERR,
						"Couldn't delete Company, there is a Company-Coupon relationship.", e.getSqlErrorDetails());
			else
				throw new UnexpectedError(MessageType.ERR, "Couldn't delete Company, UNEXPECTED ERROR.",
						e.getSqlErrorDetails());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't delete Company.",
					"Unexpected error deleting Company from database table [" + tbl_company + "] ID: " + company.getId()
							+ ".\n" + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Connection error deleting Company in database.",
					"Connection error deleting Company from database table [" + tbl_company + "] ID: " + company.getId()
							+ ".\n" + e.getDetails());
		} finally {
			try {
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		if (coupons != null) { // ****** IF COMPANY OWNED COUPONS
			for (Coupon c : coupons) {
				String companiesOwnCouponQuery = "SELECT * FROM " + tbl_join_company + " WHERE " + JOIN_COUPON_KEY
						+ " = " + c.getId() + " LIMIT 1";
				conn = null;
				try { // ****** DETECT UNOWNED COUPONS
					conn = pool.getConnection();
					if (!getResult(conn, companiesOwnCouponQuery).next()) { // ** IF NO TOHER COMPANIES OWN THIS COUPON:
						String deleteCouponQuery = "DELETE FROM " + tbl_coupon + " WHERE ID = " + c.getId();
						Connection conn2 = null;
						try { // ****** DELETE UN-OWNED COUPONS
							conn2 = pool.getConnection();
							runStatement(conn, deleteCouponQuery);
						} catch (IllegalArgumentException e) {
							handler.log("WARN: Couldn't delete Coupon.\n" + e.getMessage());
						} catch (ForeignKeyError e) { // TODO: log warning/print to console
							if (e.getKey().equals(FK_CUSTOMER_ID))
								handler.log("WARN: Couldn't delete Coupon, a Customer has this Coupon.");
							else if (e.getKey().equals(FK_COMPANY_ID))
								handler.log("WARN: Couldn't delete Coupon, a Company owns this Coupon.");
							else
								handler.log(
										"WARN: Couldn't delete Coupon, UNEXPECTED ERROR.\n" + e.getSqlErrorDetails());
						} catch (DBStatementError e) { // TODO: log warning/print to console.
							handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
						} catch (DBError e) {
							handler.log("WARN: Couldn't delete Coupon.\n" + e.getSqlErrorDetails());
						} catch (ConnectionPoolError e) {
							handler.log("WARN: Couldn't delete Coupon.\n" + e.getDetails());
						} finally {
							try {
								pool.returnConnection(conn2);
							} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
								handler.log(e.getMessage());
							}
						}
					}
				} catch (SQLException e) {
					handler.log("WARN: Couldn't detect if other Companies own Coupon,\nSKIPPING THIS COUPON DELETE.\n"
							+ e.getMessage());
				} catch (IllegalArgumentException e) {
					handler.log("ERR: Couldn't detect if other Companies own Coupon,\nSKIPPING THIS COUPON DELETE.\n"
							+ e.getMessage());
				} catch (DBStatementError e) { // TODO: log warning/print to console.
					handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
				} catch (DBError e) {
					handler.log("WARN: Couldn't detect if other Companies own Coupon,\nSKIPPING THIS COUPON DELETE.\n"
							+ e.getSqlErrorDetails());
				} catch (ConnectionPoolError e) {
					handler.log("WARN: Couldn't detect if other Companies own Coupon,\nSKIPPING THIS COUPON DELETE.\n"
							+ e.getDetails());
				} finally {
					try {
						closeAllStatements();
						pool.returnConnection(conn);
					} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
						handler.log(e.getMessage());
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean updateCompany(Company company) throws UnexpectedError {
		if (company == null)
			throw new UnexpectedError(MessageType.ERR, "Can't update record for unspecified Company",
					"Company is null");

		Connection conn = null;
		String companyUpdateQuery = "UPDATE " + tbl_company + " SET " + COMPANY_NAME + " = ?, " + COMPANY_PASSWORD
				+ " = ?, " + COMPANY_EMAIL + " = ? WHERE " + COMPANY_KEY + " = ?";
		Object[] companyUpdateArgs = new Object[] { company.getName(), company.getPassword(), company.getEmail(),
				company.getId() };

		try {
			conn = pool.getConnection();
			if (runStatement(conn, companyUpdateQuery, companyUpdateArgs) < 1)
				return doesCompanyExist(company);

		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't update Company.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DuplicateKeyError e) {
			return false;
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't update Company.", e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't update Company.", e.getMessage());
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
	public Company getCompany(long id) throws UnexpectedError {
		Company company = null;
		ResultSet companyRS = null;
		String selectCompanyQuery = "SELECT * FROM " + tbl_company + " WHERE " + COMPANY_KEY + " = " + id;
		Connection conn = null;

		try {
			conn = pool.getConnection();
			companyRS = getResult(conn, selectCompanyQuery);

			if (companyRS.next()) {
				company = new Company(companyRS.getString(COMPANY_NAME), companyRS.getString(COMPANY_PASSWORD),
						companyRS.getString(COMPANY_EMAIL), companyRS.getLong(COMPANY_KEY));
				ArrayList<Coupon> coupons = null;
				// ********************************** RUN OVER Company COUPONS
				try {
					coupons = getCoupons(company);
				} catch (UnexpectedError e) {
					handler.log(e.getMessage() + "\n" + e.getDetails());
				}
				if (coupons != null) {
					company.setCoupons(coupons);
				}
			} else
				return company;
			// throw new CompanyNotFound(MessageType.ERR, tbl_company, id);

		} catch (SQLException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Company.", e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Company.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Company.", e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Company.", e.getDetails());
		} finally {
			try {
				closeAllStatements();
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		return company;
	}

	@Override
	public Company getCompanyOnly(long id) throws UnexpectedError {
		Company company = null;
		ResultSet companyRS = null;
		String selectCompanyQuery = "SELECT * FROM " + tbl_company + " WHERE " + COMPANY_KEY + " = " + id;
		Connection conn = null;

		try {
			conn = pool.getConnection();
			companyRS = getResult(conn, selectCompanyQuery);

			if (companyRS.next()) {
				company = new Company(companyRS.getString(COMPANY_NAME), companyRS.getString(COMPANY_PASSWORD),
						companyRS.getString(COMPANY_EMAIL), companyRS.getLong(COMPANY_KEY));
			} else
				return company;
		} catch (SQLException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Company.", e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Company.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Company.", e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for specifc Company.", e.getDetails());
		} finally {
			try {
				closeAllStatements();
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		return company;
	}

	@Override
	public ArrayList<Company> getAllCompanies() throws UnexpectedError {
		ArrayList<Company> companies = new ArrayList<>();
		this.keepStatementOpen = true;
		ResultSet companiesRS = null;
		String selectCompaniesQuery = "SELECT * FROM " + tbl_company;

		Connection conn = null;

		try {
			conn = pool.getConnection();
			companiesRS = getResult(conn, selectCompaniesQuery);

			while (companiesRS.next()) {
				try {
					Company company = new Company(companiesRS.getString(COMPANY_NAME),
							companiesRS.getString(COMPANY_PASSWORD), companiesRS.getString(COMPANY_EMAIL),
							companiesRS.getLong(COMPANY_KEY));
					ArrayList<Coupon> coupons = null;
					// ********************************** RUN OVER COMPANY COUPONS
					try {
						coupons = getCoupons(company);
					} catch (UnexpectedError e) {
						handler.log(e.getMessage() + "\n" + e.getDetails());
					}
					if (coupons != null) {
						company.setCoupons(coupons);
					}

					companies.add(company);
				} catch (SQLException e) { // TODO: log warning/print to console
					handler.log("WARN: Could't get data for specific Company in Companies query.\n" + e.getMessage());
				}
			}
			if (companies.size() < 1)
				return null;
			// throw new EmptyItemList(MessageType.ERR, ItemType.COMPANY, tbl_company);

		} catch (SQLException e) {
			if (companies.size() < 1)
				throw new UnexpectedError(MessageType.ERR, "Could't get data for Companies query.", e.getMessage());
			else // TODO: log warning/print to console
				handler.log("WARN: Could't get data for all Companies query.\n" + e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for all Companies query.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for all Companies query.",
					e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Could't get data for all Companies query.", e.getDetails());
		} finally {
			try {
				this.keepStatementOpen = false;
				closeAllStatements();
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}

		return companies;
	}

	@Override
	public ArrayList<Coupon> getCoupons(CouponType[] types, Company company) throws UnexpectedError {
		return getCoupons(false, types, company);
	}

	@Override
	public ArrayList<Coupon> getCoupons(CouponType[] types, double maxPrice, Company company) throws UnexpectedError {
		return getCoupons(false, types, maxPrice, company);
	}

	@Override
	public ArrayList<Coupon> getCoupons(double maxPrice, Company company) throws UnexpectedError {
		return getCoupons(null, maxPrice, company);
	}

	@Override
	public ArrayList<Coupon> getCoupons(Company company) throws UnexpectedError {
		return getCoupons(null, company);
	}

	@Override
	public ArrayList<Coupon> getCouponsByType(CouponType type, Company company) throws UnexpectedError {
		return getCoupons(new CouponType[] { type }, company);
	}

	@Override
	public ArrayList<Coupon> getCouponsByType(CouponType type, double maxPrice, Company company)
			throws UnexpectedError {
		return getCoupons(new CouponType[] { type }, maxPrice, company);
	}

	@Override
	public ArrayList<Coupon> getArchivedCoupons(CouponType[] types, Company company) throws UnexpectedError {
		return getCoupons(true, types, company);
	}

	@Override
	public ArrayList<Coupon> getArchivedCoupons(CouponType[] types, double maxPrice, Company company)
			throws UnexpectedError {
		return getCoupons(true, types, maxPrice, company);
	}

	@Override
	public ArrayList<Coupon> getArchivedCoupons(double maxPrice, Company company) throws UnexpectedError {
		return getArchivedCoupons(null, maxPrice, company);
	}

	@Override
	public ArrayList<Coupon> getArchivedCoupons(Company company) throws UnexpectedError {
		return getArchivedCoupons(null, company);
	}

	@Override
	public ArrayList<Coupon> getArchivedCouponsByType(CouponType type, Company company) throws UnexpectedError {
		return getArchivedCoupons(new CouponType[] { type }, company);
	}

	@Override
	public ArrayList<Coupon> getArchivedCouponsByType(CouponType type, double maxPrice, Company company)
			throws UnexpectedError {
		return getArchivedCoupons(new CouponType[] { type }, maxPrice, company);
	}

	@Override
	public final Company login(String compName, String password) throws UnexpectedError {
		if (compName == null || compName.equals("") || password == null || password.equals(""))
			throw new UnexpectedError(MessageType.ERR, "Can't request login for unspecified username or password",
					"Either username or password are empty/null");

		String loginQuery = "SELECT * FROM " + tbl_company + " WHERE " + COMPANY_NAME + " = '" + compName.toLowerCase()
				+ "' AND " + COMPANY_PASSWORD + " = '" + password + "'";
		ResultSet result = null;
		Company company = null;
		Connection conn = null;

		try {
			conn = pool.getConnection();
			if ((result = getResult(conn, loginQuery)).next()) {
				company = new Company(result.getString(COMPANY_NAME), result.getString(COMPANY_PASSWORD),
						result.getString(COMPANY_EMAIL), result.getLong(COMPANY_KEY));
				ArrayList<Coupon> coupons = null;
				// ********************************** RUN OVER COMPANY COUPONS
				try {
					coupons = getCoupons(company);
				} catch (UnexpectedError e) {
					handler.log(e.getMessage() + "\n" + e.getDetails());
				}
				if (coupons != null) {
					company.setCoupons(coupons);
				}
				return company;
			}
		} catch (SQLException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't verify login information.", e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't verify login information.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't verify login information.", e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't send login information.", e.getDetails());
		} finally {
			try {
				pool.returnConnection(conn);
			} catch (ConnectionPoolError e) {// TODO: log warning/print to console.
				handler.log(e.getMessage());
			}
		}
		return null;
	}

	@Override
	public final boolean doesCompanyExist(Company company) throws UnexpectedError {
		if (company == null)
			throw new UnexpectedError(MessageType.ERR, "Can't read record for unspecified Company", "Company is null");

		String doesCompanyExistQuery = "SELECT * FROM " + tbl_company + " WHERE " + COMPANY_KEY + " = "
				+ company.getId() + " LIMIT 1";
		Connection conn = null;

		try {
			conn = pool.getConnection();
			return getResult(conn, doesCompanyExistQuery).next();
		} catch (SQLException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't detect if Company exists.", e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't detect if Company exists.", e.getMessage());
		} catch (DBStatementError e) { // TODO: log warning/print to console.
			handler.log("WARN: " + e.getDetails() + "\n" + e.getSqlErrorDetails());
		} catch (DBError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't detect if Company exists.", e.getSqlErrorDetails());
		} catch (ConnectionPoolError e) {
			throw new UnexpectedError(MessageType.ERR, "Couldn't detect if Company exists.", e.getDetails());
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