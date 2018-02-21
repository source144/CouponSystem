package Facade;

import java.util.ArrayList;

import Bean.Company;
import Bean.Coupon;
import Bean.CouponType;
import DAO.CompanyDAO;
import DAO.CouponDAO;
import DB.CompanyDBDAO;
import DB.CouponDBDAO;
import Exceptions.ItemType;
import Exceptions.MessageType;
import Exceptions.AlreadyExists.CompanyAlreadyExists;
import Exceptions.AlreadyExists.CouponAlreadyExists;
import Exceptions.DoesntOwn.CompanyDoesntOwnCoupon;
import Exceptions.General.BadUsernamePassword;
import Exceptions.General.InvalidSession;
import Exceptions.General.UnexpectedError;
import Exceptions.ListEmpty.EmptyFilteredList;
import Exceptions.ListEmpty.EmptyItemList;
import Exceptions.NotFound.CompanyNotFound;
import Exceptions.NotFound.CouponNotFound;

/**
 * The Company facade.<br>
 * <br>
 * Contains method to create, update, remove and get {@link Coupons} for the
 * logged in {@link Company}. <br>
 * Requires a login method to get instance.<br>
 * <em>All methods <strong>require</strong> a valid instance/session.</em>
 * 
 * @author Gonen Matias
 * @version 1.0 30/01/2018
 *
 */
public final class CompanyFacade implements CouponClientFacade {
	private static final CompanyDAO companyUtil = new CompanyDBDAO();
	private static final CouponDAO couponUtil = new CouponDBDAO();
	private Company company = null;

	/**
	 * Constructs a new {@link CompanyFacade} for a {@link Company}
	 * 
	 * @param company
	 *            the company for the session
	 */
	private CompanyFacade(Company company) {
		this.company = company;
	}

	/**
	 * Gets a valid 'session' of {@link CompanyFacade} by logging in.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return returns an new instance of CompanyFacade if the username and password
	 *         are correct
	 * @throws BadUsernamePassword
	 *             throws a {@link BadUsernamePassword} exception if username or
	 *             password are incorrect
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 */
	public static CompanyFacade login(String username, String password) throws BadUsernamePassword, UnexpectedError {
		Company company;
		if ((company = companyUtil.login(username, password)) != null)
			return new CompanyFacade(company);
		else
			throw new BadUsernamePassword();
	}

	/**
	 * Validates this CompanyFacade's session.<br>
	 * <br>
	 * Checks if current value of this CompanyFacade's company is not null.<br>
	 * Syncs it with the database and checks again if it's not null.<br>
	 * <br>
	 * 
	 * if any of the values are null, the session is not valid.
	 * 
	 * @return whether this CompanyFacade has a valid session (true) or not (false).
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if for some reason couldn't
	 *             validate reach the data.
	 */
	private boolean hasSession() throws InvalidSession {
		try {
			if (this.company == null)
				return false;
			else if ((this.company = companyUtil.getCompanyOnly(this.company.getId())) == null)
				return false;
			return true;
		} catch (UnexpectedError e) {
			throw new InvalidSession(MessageType.ERR, ClientType.COMPANY, "Couldn't validate session");
		}
	}

	/**
	 * Creates a {@link Coupon} and links it to this Company
	 * 
	 * @param coupon
	 *            the coupon
	 * @throws CouponAlreadyExists
	 *             throws {@link CouponAlreadyExists} if the ID key or Coupon title
	 *             already exists.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             CompanyFacade is invalid.
	 */
	public void createCoupon(Coupon coupon) throws CouponAlreadyExists, UnexpectedError, InvalidSession {
		if (!hasSession())
			throw new InvalidSession(MessageType.ERR, ClientType.COMPANY);

		if (!couponUtil.createCoupon(coupon, this.company))
			throw new CouponAlreadyExists(MessageType.ERR, coupon.getTitle(), coupon.getId());
	}

	/**
	 * Removes a {@link Coupon}
	 * 
	 * @param coupon
	 *            the coupon
	 * @throws CouponNotFound
	 *             throws {@link CouponNotFound} if the Coupon doesn't exist.
	 * @throws CompanyDoesntOwnCoupon
	 *             throws {@link CouponNotFound} if the coupon is not owned by this
	 *             company.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             CompanyFacade is invalid.
	 */
	public void removeCoupon(Coupon coupon)
			throws CouponNotFound, CompanyDoesntOwnCoupon, UnexpectedError, InvalidSession {
		if (!hasSession())
			throw new InvalidSession(MessageType.ERR, ClientType.COMPANY);

		if (couponUtil.companyOwnsCoupon(coupon, this.company)) {
			if (!couponUtil.removeCoupon(coupon))
				throw new CouponNotFound(MessageType.ERR, coupon.getTitle(), coupon.getId());

		} else if (!couponUtil.doesCouponExist(coupon))
			throw new CouponNotFound(MessageType.ERR, coupon.getTitle(), coupon.getId());
		else
			throw new CompanyDoesntOwnCoupon(MessageType.ERR, this.company.getName(), this.company.getId(),
					coupon.getTitle(), coupon.getId());
	}

	/**
	 * Updates a {@link Coupon}
	 * 
	 * @param coupon
	 *            the coupon
	 * @throws CouponNotFound
	 *             throws {@link CouponNotFound} if the coupon doesn't exist.
	 * @throws CompanyDoesntOwnCoupon
	 *             throws {@link CouponNotFound} if the coupon is not owned by this
	 *             company.
	 * @throws CouponAlreadyExists
	 *             throws {@link CouponAlreadyExists} if the coupon title is already
	 *             taken.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             CompanyFacade is invalid.
	 */
	public void updateCoupon(Coupon coupon)
			throws CouponNotFound, CompanyDoesntOwnCoupon, CouponAlreadyExists, UnexpectedError, InvalidSession {
		if (!hasSession())
			throw new InvalidSession(MessageType.ERR, ClientType.COMPANY);

		if (couponUtil.companyOwnsCoupon(coupon, this.company)) {
			if (!couponUtil.updateCoupon(coupon))
				throw new CouponAlreadyExists(MessageType.ERR, coupon.getTitle(), coupon.getId(),
						"Coupon title '" + coupon.getTitle() + "' already exists.");
		} else if (!couponUtil.doesCouponExist(coupon))
			throw new CouponNotFound(MessageType.ERR, coupon.getTitle(), coupon.getId());
		else
			throw new CompanyDoesntOwnCoupon(MessageType.ERR, this.company.getName(), this.company.getId(),
					coupon.getTitle(), coupon.getId());
	}

	/**
	 * Removes a {@link Coupon}'s link to this {@link Company}.
	 * 
	 * @param coupon
	 *            the coupon
	 * @throws CouponNotFound
	 *             throws {@link CouponNotFound} if the coupon doesn't exist.
	 * @throws CompanyDoesntOwnCoupon
	 *             throws {@link CouponNotFound} if the coupon is not owned by this
	 *             company.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             CompanyFacade is invalid.
	 */
	public void removeCouponFromCompany(Coupon coupon)
			throws CouponNotFound, CompanyDoesntOwnCoupon, UnexpectedError, InvalidSession {
		if (!hasSession())
			throw new InvalidSession(MessageType.ERR, ClientType.COMPANY);

		if (!couponUtil.removeCouponFromCompany(coupon, this.company)) {
			if (!couponUtil.doesCouponExist(coupon))
				throw new CouponNotFound(MessageType.ERR, coupon.getTitle(), coupon.getId());
			else
				throw new CompanyDoesntOwnCoupon(MessageType.ERR, this.company.getName(), this.company.getId(),
						coupon.getTitle(), coupon.getId());
		}
	}

	/**
	 * Gets the {@link Company} this CompanyFacade session is using
	 * 
	 * @return the company
	 * @throws CompanyNotFound
	 *             throws {@link CompanyNotFound} if this company's data couldn't be
	 *             found.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             CompanyFacade is invalid.
	 * @throws UnexpectedError
	 */
	public Company getCompany() throws InvalidSession, UnexpectedError {
		if (this.company == null)
			if ((this.company = companyUtil.getCompany(this.company.getId())) == null)
				throw new InvalidSession(MessageType.ERR, ClientType.COMPANY);

		return this.company;
	}

	/**
	 * Gets a list of {@link Coupon}s owned by this {@link CompanyFacade}'s session
	 * filtered by types and max price
	 * 
	 * @param types
	 *            the types filter
	 * @param maxPrice
	 *            the max price
	 * @return the filtered list of coupons owned by this company
	 * @throws EmptyFilteredList
	 *             throws {@link EmptyFilteredList} if the list is empty.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public ArrayList<Coupon> getCouponsBy(CouponType[] types, double maxPrice)
			throws EmptyFilteredList, UnexpectedError, InvalidSession {
		if (!hasSession())
			throw new InvalidSession(MessageType.ERR, ClientType.COMPANY);

		ArrayList<Coupon> coupons = companyUtil.getCoupons(types, maxPrice, this.company);
		if (coupons == null) {
			String query = "COMP_ID=" + this.company.getId();
			if (types != null) {
				if (types.length > 0)
					query += " AND TYPE= " + types[0].toString();
				for (int i = 1; i < types.length; i++)
					query += ", " + types[i].toString();
			}
			query += ", AND Max price: " + maxPrice + ";";
			throw new EmptyFilteredList(MessageType.ERR, ItemType.COUPON, query);
		}
		return coupons;
	}

	/**
	 * Gets a list of {@link Coupon}s owned by this {@link CompanyFacade}'s session
	 * filtered by types
	 * 
	 * @param types
	 *            the types filter
	 * @return the filtered list of coupons owned by this company
	 * @throws EmptyFilteredList
	 *             throws {@link EmptyFilteredList} if the list is empty.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public ArrayList<Coupon> getCouponsBy(CouponType[] types)
			throws EmptyFilteredList, UnexpectedError, InvalidSession {
		if (!hasSession())
			throw new InvalidSession(MessageType.ERR, ClientType.COMPANY);

		ArrayList<Coupon> coupons = companyUtil.getCoupons(types, this.company);
		if (coupons == null) {
			String query = "COMP_ID=" + this.company.getId();
			if (types != null) {
				if (types.length > 0)
					query += " AND TYPE= " + types[0].toString();
				for (int i = 1; i < types.length; i++)
					query += ", " + types[i].toString();
			}
			query += ";";
			throw new EmptyFilteredList(MessageType.ERR, ItemType.COUPON, query);
		}
		return coupons;
	}

	/**
	 * Gets a list of {@link Coupon}s owned by this {@link CompanyFacade}'s session
	 * filtered by max price
	 * 
	 * @param maxPrice
	 *            the max price
	 * @return the filtered list of coupons owned by this company
	 * @throws EmptyFilteredList
	 *             throws {@link EmptyFilteredList} if the list is empty.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public ArrayList<Coupon> getCouponsByMaxPrice(double maxPrice)
			throws EmptyFilteredList, UnexpectedError, InvalidSession {
		return getCouponsBy(null, maxPrice);
	}

	/**
	 * Gets a list of {@link Coupon}s owned by this {@link CompanyFacade}'s session
	 * filtered by a certain type and max price
	 * 
	 * @param type
	 *            the type
	 * @param maxPrice
	 *            the max price
	 * @return the filtered list of coupons owned by this company
	 * @throws EmptyFilteredList
	 *             throws {@link EmptyFilteredList} if the list is empty.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public ArrayList<Coupon> getCouponsByMaxPrice(CouponType type, double maxPrice)
			throws EmptyFilteredList, UnexpectedError, InvalidSession {
		return getCouponsBy(new CouponType[] { type }, maxPrice);
	}

	/**
	 * Gets a list of {@link Coupon}s owned by this {@link CompanyFacade}'s session
	 * filtered by a certain type
	 * 
	 * @param type
	 *            the type
	 * @return the filtered list of coupons owned by this company
	 * @throws EmptyFilteredList
	 *             throws {@link EmptyFilteredList} if the list is empty.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public ArrayList<Coupon> getCouponsByType(CouponType type)
			throws EmptyFilteredList, UnexpectedError, InvalidSession {
		return getCouponsBy(new CouponType[] { type });
	}

	/**
	 * Gets a list of all {@link Coupon}s owned by this {@link CompanyFacade}'s
	 * session
	 * 
	 * @return the list of coupons owned by this company
	 * @throws EmptyItemList
	 *             throws {@link EmptyItemList} if there are no Coupons.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public ArrayList<Coupon> getCoupons() throws EmptyFilteredList, UnexpectedError, InvalidSession {
		return getCouponsBy(null);
	}

	/**
	 * Updates this {@link Company}'s password.
	 * 
	 * @param password
	 *            the new password
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public void setPassword(String password) throws CompanyNotFound, UnexpectedError, InvalidSession {
		if (!hasSession())
			throw new InvalidSession(MessageType.ERR, ClientType.COMPANY);

		if (!this.company.getPassword().equals(password)) {
			this.company.setPassword(password);
			companyUtil.updateCompany(this.company);
		}
	}

	/**
	 * Updates this {@link Company}'s email.
	 * 
	 * @param email
	 *            the new email
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public void setEmail(String email) throws UnexpectedError, InvalidSession {
		if (!hasSession())
			throw new InvalidSession(MessageType.ERR, ClientType.COMPANY);

		if (!this.company.getEmail().equals(email)) {
			this.company.setEmail(email);
			companyUtil.updateCompany(this.company);
		}
	}

	/**
	 * Updates this {@link Company}'s name.
	 * 
	 * @param name
	 *            the new email
	 * @throws CompanyAlreadyExists
	 *             throws {@link CompanyAlreadyExists} if the name of the company is
	 *             already taken.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public void setName(String name) throws CompanyAlreadyExists, UnexpectedError, InvalidSession {
		if (!hasSession())
			throw new InvalidSession(MessageType.ERR, ClientType.COMPANY);

		if (!this.company.getName().equals(name)) {
			String oldName = this.company.getName();
			this.company.setName(name);
			if (!companyUtil.updateCompany(this.company)) {
				this.company.setName(oldName);
				throw new CompanyAlreadyExists(MessageType.ERR, name, this.company.getId(),
						"Company name '" + name + "' already exists.");
			}
		}
	}
}
