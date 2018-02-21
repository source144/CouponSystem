package DAO;

import java.util.ArrayList;

import Bean.Company;
import Bean.Coupon;
import Bean.CouponType;
import Bean.Customer;
import Exceptions.General.UnexpectedError;

/**
 * 
 * Coupon data access object interface
 * 
 * Includes all the the basic actions to be performed on data
 * 
 * @author Gonen Matias
 * @version 1.0 30/01/2018
 */
public interface CouponDAO {
	/**
	 * Stores a {@link Coupon} in database.
	 * 
	 * @param coupon
	 *            the coupon
	 * @return either (1) returns <em><ins>true</ins></em> if a record has been
	 *         added, or (2) <em><ins>false</ins></em> if not.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to modify data.
	 */
	public boolean createCoupon(Coupon coupon) throws UnexpectedError;

	/**
	 * Stores a {@link Coupon} in database and links it to a {@link Company}.<br>
	 * <mark><ins>NOTE:</ins><mark> <em>does not specify if successfully
	 * linked</em>.
	 * 
	 * @param coupon
	 *            the coupon
	 * @return either (1) returns <em><ins>true</ins></em> if a record has been
	 *         added, or (2) <em><ins>false</ins></em> if not.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to modify data.
	 */
	public boolean createCoupon(Coupon coupon, Company company) throws UnexpectedError;

	/**
	 * Stores an existing {@link Coupon} from the database, along with it's join
	 * table records, in the archives.<br>
	 * <mark><ins>NOTE:</ins><mark> <em>this does not delete the original coupon
	 * record and all of it's related data</em>.
	 * 
	 * @param coupon
	 *            the coupon
	 * @return either (1) returns <em><ins>true</ins></em> if a record has been
	 *         added, or (2) <em><ins>false</ins></em> if not.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to modify data.
	 */
	public boolean addCouponToArchive(Coupon coupon) throws UnexpectedError;

	/**
	 * removes {@link Coupon} and all data relating to it from database.
	 * 
	 * @param coupon
	 *            the coupon
	 * @return either (1) returns <em><ins>true</ins></em> if a record has been
	 *         deleted, or (2) <em><ins>false</ins></em> if not.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to modify data.
	 */
	public boolean removeCoupon(Coupon coupon) throws UnexpectedError;

	/**
	 * Updates a {@link Coupon}'s information in database.
	 * 
	 * @param coupon
	 *            the coupon
	 * @return either (1) returns <em><ins>true</ins></em> whether an update
	 *         <em><strong>could</strong> be made</em></strong> to a record, or (2)
	 *         <em><ins>false</ins></em> if the update <em>could
	 *         <strong>NOT</strong> be made</em> (no record exists).
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to modify data.
	 */
	public boolean updateCoupon(Coupon coupon) throws UnexpectedError;

	/**
	 * Gets a {@link Coupon} from the database by id.<br>
	 * 
	 * @param id
	 *            the id
	 * @return returns the {@link Coupon} or null if {@link Coupon} does not exist.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public Coupon getCoupon(long id) throws UnexpectedError;

	/**
	 * Gets an archived {@link Coupon} from the database by id.<br>
	 * 
	 * @param id
	 *            the id
	 * @return returns the archived {@link Coupon} or null if {@link Coupon} does
	 *         not exist.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public Coupon getArchivedCoupon(long id) throws UnexpectedError;

	/**
	 * Gets a list of all {@link Coupon}s from the database.
	 * 
	 * @return returns a list of all coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getCoupons(CouponType[] types) throws UnexpectedError;

	/**
	 * Gets a list of all {@link Coupon}s that fit at least one of the types and
	 * have a price lower than the specified price from the database.
	 * 
	 * @param types
	 *            the types
	 * @param maxPrice
	 *            the max price
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getCoupons(CouponType[] types, double maxPrice) throws UnexpectedError;

	/**
	 * Gets a list of all {@link Coupon}s that have a price lower than the specified
	 * price from the database.
	 * 
	 * @param types
	 *            the types
	 * @param maxPrice
	 *            the max price
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getCoupons(double maxPrice) throws UnexpectedError;

	/**
	 * Gets a list of all {@link Coupon}s of the specified type from the database.
	 * 
	 * 
	 * @param type
	 *            the type
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getCouponsByType(CouponType type) throws UnexpectedError;

	/**
	 * Gets a list of all {@link Coupon}s from the database.
	 * 
	 * @return returns a list of all coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getAllCoupons() throws UnexpectedError;

	/**
	 * Gets a list of all archived {@link Coupon}s from the database.
	 * 
	 * @return returns a list of all archived coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getArchivedCoupons(CouponType[] types) throws UnexpectedError;

	/**
	 * Gets a list of all archived {@link Coupon}s that fit at least one of the
	 * types and have a price lower than the specified price from the database.
	 * 
	 * @param types
	 *            the types
	 * @param maxPrice
	 *            the max price
	 * @return the list of archived coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getArchivedCoupons(CouponType[] types, double maxPrice) throws UnexpectedError;

	/**
	 * Gets a list of all archived {@link Coupon}s that have a price lower than the
	 * specified price from the database.
	 * 
	 * @param types
	 *            the types
	 * @param maxPrice
	 *            the max price
	 * @return the list of archived coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getArchivedCoupons(double maxPrice) throws UnexpectedError;

	/**
	 * Gets a list of all archived {@link Coupon}s of the specified type from the
	 * database.
	 * 
	 * 
	 * @param type
	 *            the type
	 * @return the list of archived coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getArchivedCouponsByType(CouponType type) throws UnexpectedError;

	/**
	 * Gets a list of all archived {@link Coupon}s from the database.
	 * 
	 * @return returns a list of all archived coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getAllArchivedCoupons() throws UnexpectedError;

	/**
	 * Creates a link between a {@link Coupon} and {@link Company} in the database.
	 * 
	 * @param coupon
	 *            the coupon
	 * @param company
	 *            the company
	 * @return either (1) returns <em><ins>true</ins></em> if the link was created,
	 *         or (2) <em><ins>false</ins></em> if not.<br>
	 *         (already exists, coupon/company do not exist)
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to modify data.
	 */
	public boolean addCouponToCompany(Coupon coupon, Company company) throws UnexpectedError;

	/**
	 * Creates a link between a {@link Coupon} and {@link Customer} in the database.
	 * 
	 * @param coupon
	 *            the coupon
	 * @param customer
	 *            the customer
	 * @return either (1) returns <em><ins>true</ins></em> if the link was created,
	 *         or (2) <em><ins>false</ins></em> if not.<br>
	 *         (already exists, coupon/customer do not exist)
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to modify data.
	 */
	public boolean addCouponToCustomer(Coupon coupon, Customer customer) throws UnexpectedError;

	/**
	 * removes a link between a {@link Coupon} and {@link Company} in the database.
	 * 
	 * @param coupon
	 *            the coupon
	 * @param company
	 *            the company
	 * @return either (1) returns <em><ins>true</ins></em> if the link was removed,
	 *         or (2) <em><ins>false</ins></em> if not.<br>
	 *         (link does not exist)
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to modify data.
	 */
	public boolean removeCouponFromCompany(Coupon coupon, Company company) throws UnexpectedError;

	/**
	 * removes a link between a {@link Coupon} and {@link Company} in the database.
	 * 
	 * @param coupon
	 *            the coupon
	 * @param customer
	 *            the customer
	 * @return either (1) returns <em><ins>true</ins></em> if the link was removed,
	 *         or (2) <em><ins>false</ins></em> if not.<br>
	 *         (link does not exist)
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to modify data.
	 */
	public boolean removeCouponFromCustomer(Coupon coupon, Customer customer) throws UnexpectedError;

	/**
	 * A quick method to detect if a {@link Company} owns a {@link Coupon} in the
	 * database.
	 * 
	 * @param coupon
	 *            the coupon
	 * @param company
	 *            the company
	 * @return either (1) returns <em><ins>true</ins></em> if the company owns the
	 *         coupon, or (2) <em><ins>false</ins></em> if not.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public boolean companyOwnsCoupon(Coupon coupon, Company company) throws UnexpectedError;

	/**
	 * Gets the amount of {@link Coupon}s left by calculating data from the
	 * database.
	 * 
	 * 
	 * @param coupon
	 *            the coupon
	 * @return either (1) returns the amount of coupons left in stock for the
	 *         specified Coupon or (2) returns 0.<br>
	 *         <del><em>(3) returns -1 if coupon does not exist</em></del>.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public int couponsLeft(Coupon coupon) throws UnexpectedError;

	/**
	 * A quick method to detect if a {@link Coupon} exists in the database.
	 * 
	 * 
	 * @param coupon
	 *            the coupon
	 * @return either (1) returns <em><ins>true</ins></em> if the coupon exists, or
	 *         (2) <em><ins>false</ins></em> if not.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public boolean doesCouponExist(Coupon coupon) throws UnexpectedError;
}
