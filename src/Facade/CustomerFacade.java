package Facade;

import java.sql.Date;
import java.util.ArrayList;

import Bean.Coupon;
import Bean.CouponType;
import Bean.Customer;
import DAO.CouponDAO;
import DAO.CustomerDAO;
import DB.CouponDBDAO;
import DB.CustomerDBDAO;
import Exceptions.ItemType;
import Exceptions.MessageType;
import Exceptions.AlreadyExists.CustomerAlreadyExists;
import Exceptions.AlreadyOwns.CustomerAlreadyOwnsCoupon;
import Exceptions.CouponException.CouponExpired;
import Exceptions.CouponException.CouponStockEmpty;
import Exceptions.General.BadUsernamePassword;
import Exceptions.General.InvalidSession;
import Exceptions.General.UnexpectedError;
import Exceptions.ListEmpty.EmptyFilteredList;
import Exceptions.NotFound.CompanyNotFound;
import Exceptions.NotFound.CouponNotFound;

/**
 * The Customer facade.<br>
 * <br>
 * Contains method to create, update, remove and get {@link Coupons} for the
 * logged in {@link Customer}. <br>
 * Requires a login method to get instance.<br>
 * <em>All methods <strong>require</strong> a valid instance/session.</em>
 * 
 * @author Gonen Matias
 * @version 1.0 30/01/2018
 *
 */
public final class CustomerFacade implements CouponClientFacade {
	private static final CustomerDAO customerUtil = new CustomerDBDAO();
	private static final CouponDAO couponUtil = new CouponDBDAO();
	private Customer customer = null;

	/**
	 * Constructs a new {@link CustomerFacade} for a {@link Customer}
	 * 
	 * @param customer
	 *            the customer for the session
	 */
	private CustomerFacade(Customer customer) throws UnexpectedError {
		this.customer = customer;
	}

	/**
	 * Gets a valid 'session' of {@link CustomerFacade} by logging in.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return returns an new instance of CustomerFacade if the username and
	 *         password are correct
	 * @throws BadUsernamePassword
	 *             throws a {@link BadUsernamePassword} exception if username or
	 *             password are incorrect
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 */
	public static CustomerFacade login(String username, String password) throws BadUsernamePassword, UnexpectedError {
		Customer customer;
		if ((customer = customerUtil.login(username, password)) != null)
			return new CustomerFacade(customer);
		else
			throw new BadUsernamePassword();
	}

	/**
	 * Validates this CustomerFacade's session.<br>
	 * <br>
	 * Checks if current value of this CustomerFacade's customer is not null.<br>
	 * Syncs it with the database and checks again if it's not null.<br>
	 * <br>
	 * 
	 * if any of the values are null, the session is not valid.
	 * 
	 * @return whether this CustomerFacade has a valid session (true) or not
	 *         (false).
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if for some reason couldn't
	 *             validate reach the data.
	 */
	private boolean hasSession() throws InvalidSession {
		try {
			if (this.customer == null)
				return false;
			else if ((this.customer = customerUtil.getCustomerOnly(this.customer.getId())) == null)
				return false;
			return true;
		} catch (UnexpectedError e) {
			throw new InvalidSession(MessageType.ERR, ClientType.COMPANY, "Couldn't validate session");
		}
	}

	/**
	 * Purchases a {@link Coupon} for this Customer
	 * @param coupon the coupon
	 * @throws CompanyNotFound
	 *             throws {@link CouponNotFound} if the Coupon doesn't exist.
	 * @throws CustomerAlreadyOwnsCoupon throws {@link CustomerAlreadyOwnsCoupon} if this customer already owns the coupon.
	 * @throws CouponStockEmpty throws {@link CouponStockEmpty} if the coupon is out of stock.
	 * @throws CouponExpired throws {@link CouponExpired} if the coupon has expired
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             CompanyFacade is invalid.
	 */
	public void purchaseCoupon(Coupon coupon) throws CouponNotFound, CustomerAlreadyOwnsCoupon, CouponStockEmpty,
			CouponExpired, UnexpectedError, InvalidSession {
		if (!hasSession())
			throw new InvalidSession(MessageType.ERR, ClientType.CUSTOMER);
		
		Coupon couponFromDB = couponUtil.getCoupon(coupon.getId());

		if (couponFromDB != null) {
			if (couponUtil.couponsLeft(coupon) < 1)
				throw new CouponStockEmpty(MessageType.ERR, coupon.getTitle(), coupon.getId());
			
			if (couponFromDB.getEndDate().before(new Date(System.currentTimeMillis())))
				throw new CouponExpired(MessageType.ERR, couponFromDB.getTitle(), couponFromDB.getId(),
					couponFromDB.getEndDate());

			if (!couponUtil.addCouponToCustomer(coupon, this.customer))
				throw new CustomerAlreadyOwnsCoupon(MessageType.ERR, this.customer.getName(), this.customer.getId(),
					coupon.getTitle(), coupon.getId());
		} else 
			throw new CouponNotFound(MessageType.ERR, coupon.getTitle(), coupon.getId());
	}

	/**
	 * Gets a all of the purchased {@link Coupon}s this customer has, filtered by
	 * types and max price
	 * 
	 * @param types
	 *            the types filter
	 * @param maxPrice
	 *            the max price
	 * @return the filtered list of coupons owned by this customer
	 * @throws EmptyFilteredList
	 *             throws {@link EmptyFilteredList} if the list is empty.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public ArrayList<Coupon> getPurchasedCoupons(CouponType[] types, double maxPrice)
			throws EmptyFilteredList, UnexpectedError, InvalidSession {
		if (!hasSession())
			throw new InvalidSession(MessageType.ERR, ClientType.CUSTOMER);

		ArrayList<Coupon> result = new ArrayList<>();
		
		ArrayList<Coupon> coupons = customerUtil.getCoupons(types, maxPrice, this.customer);
		ArrayList<Coupon> oldCoupons = customerUtil.getArchivedCoupons(types, maxPrice, this.customer);

		if (coupons != null) {
			result.addAll(coupons);
			if (oldCoupons != null)
				result.addAll(oldCoupons);
		}
		else if (oldCoupons != null)
			result.addAll(oldCoupons);

		if (result.size() < 1) {
			String query = "CUST_ID=" + this.customer.getId();
			if (types != null) {
				if (types.length > 0)
					query += " AND TYPE= " + types[0].toString();
				for (int i = 1; i < types.length; i++)
					query += ", " + types[i].toString();
			}
			query += ", AND Max price: " + maxPrice + ";";
			throw new EmptyFilteredList(MessageType.ERR, ItemType.COUPON, query);
		}
		return result;
	}

	/**
	 * Gets a all of the purchased {@link Coupon}s this customer has, filtered by
	 * types
	 * 
	 * @param types
	 *            the types filter
	 * @return the filtered list of coupons owned by this customer
	 * @throws EmptyFilteredList
	 *             throws {@link EmptyFilteredList} if the list is empty.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public ArrayList<Coupon> getPurchasedCoupons(CouponType[] types)
			throws EmptyFilteredList, UnexpectedError, InvalidSession {
		if (!hasSession())
			throw new InvalidSession(MessageType.ERR, ClientType.CUSTOMER);

		ArrayList<Coupon> result = new ArrayList<>();
		
		ArrayList<Coupon> coupons = customerUtil.getCoupons(types, this.customer);
		ArrayList<Coupon> oldCoupons = customerUtil.getArchivedCoupons(types, this.customer);

		if (coupons != null) {
			result.addAll(coupons);
			if (oldCoupons != null)
				result.addAll(oldCoupons);
		}
		else if (oldCoupons != null)
			result.addAll(oldCoupons);

		if (result.size() < 1) {
			String query = "CUST_ID=" + this.customer.getId();
			if (types != null) {
				if (types.length > 0)
					query += " AND TYPE= " + types[0].toString();
				for (int i = 1; i < types.length; i++)
					query += ", " + types[i].toString();
			}
			query += ";";
			throw new EmptyFilteredList(MessageType.ERR, ItemType.COUPON, query);
		}
		return result;
	}

	/**
	 * Gets a all of the purchased {@link Coupon}s this customer has, filtered by
	 * max price
	 * 
	 * @param maxPrice
	 *            the max price
	 * @return the filtered list of coupons owned by this customer
	 * @throws EmptyFilteredList
	 *             throws {@link EmptyFilteredList} if the list is empty.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public ArrayList<Coupon> getPurchasedCouponsByMaxPrice(double maxPrice)
			throws EmptyFilteredList, UnexpectedError, InvalidSession {
		return getPurchasedCoupons(null, maxPrice);
	}

	/**
	 * Gets a all of the purchased {@link Coupon}s this customer has, filtered by a
	 * certain type and max price
	 * 
	 * @param type
	 *            the type
	 * @param maxPrice
	 *            the max price
	 * @return the filtered list of coupons owned by this customer
	 * @throws EmptyFilteredList
	 *             throws {@link EmptyFilteredList} if the list is empty.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public ArrayList<Coupon> getPurchasedCouponsByMaxPrice(CouponType type, double maxPrice)
			throws EmptyFilteredList, UnexpectedError, InvalidSession {
		return getPurchasedCoupons(new CouponType[] { type }, maxPrice);
	}

	/**
	 * Gets a all of the purchased {@link Coupon}s this customer has, filtered by a
	 * certain type.
	 * 
	 * @param type
	 *            the type
	 * @param maxPrice
	 *            the max price
	 * @return the filtered list of coupons owned by this customer
	 * @throws EmptyFilteredList
	 *             throws {@link EmptyFilteredList} if the list is empty.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public ArrayList<Coupon> getPurchasedCouponsByType(CouponType type)
			throws EmptyFilteredList, UnexpectedError, InvalidSession {
		return getPurchasedCoupons(new CouponType[] { type });
	}

	/**
	 * Gets a all of the purchased {@link Coupon}s this customer has.
	 * 
	 * @param types
	 *            the types filter
	 * @param maxPrice
	 *            the max price
	 * @return the filtered list of coupons owned by this customer
	 * @throws EmptyFilteredList
	 *             throws {@link EmptyFilteredList} if the list is empty.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public ArrayList<Coupon> getPurchasedCoupons() throws EmptyFilteredList, UnexpectedError, InvalidSession {
		return getPurchasedCoupons(null);
	}

	/**
	 * Updates this {@link Customer}'s password.
	 * 
	 * @param password
	 *            the new password
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public void setPassword(String password) throws UnexpectedError, InvalidSession {
		if (!hasSession())
			throw new InvalidSession(MessageType.ERR, ClientType.CUSTOMER);
		
		if (!this.customer.getPassword().equals(password)) {
			this.customer.setPassword(password);
			customerUtil.updateCustomer(this.customer);
		}
	}
	
	/**
	 * Updates this {@link Customer}'s name.
	 * 
	 * @param name
	 *            the new name
	 * @throws CustomerAlreadyExists
	 *             throws {@link CustomerAlreadyExists} if the name of the customer is
	 *             already taken.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public void setName(String name) throws CustomerAlreadyExists, UnexpectedError, InvalidSession {
		if (this.customer == null)
			throw new InvalidSession(MessageType.ERR, ClientType.CUSTOMER);

		if (!this.customer.getName().equals(name)) {
			String oldName = this.customer.getName();
			this.customer.setName(name);
			if (!customerUtil.updateCustomer(this.customer)) {
				this.customer.setName(oldName);
				throw new CustomerAlreadyExists(MessageType.ERR, name, this.customer.getId(),
						"Customer name '" + name + "' already exists.");
			}
		}
	}
}
