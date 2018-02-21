package DAO;

import java.util.ArrayList;

import Bean.Company;
import Bean.Coupon;
import Bean.CouponType;
import Exceptions.General.UnexpectedError;

/**
 * 
 * Company data access object interface
 * 
 * Includes all the the basic actions to be performed on data
 * 
 * @author Gonen Matias
 * @version 1.0 30/01/2018
 */
public interface CompanyDAO {

	/**
	 * Stores a {@link Company} in database.
	 * 
	 * @param company
	 *            the company
	 * @return either (1) returns <em><ins>true</ins></em> if a record has been
	 *         added, or (2) <em><ins>false</ins></em> if not.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to modify data.
	 */
	public boolean createCompany(Company company) throws UnexpectedError;

	/**
	 * removes {@link Company} and all data relating to it from database.
	 * 
	 * @param company
	 *            the company
	 * @return either (1) returns <em><ins>true</ins></em> if a record has been
	 *         deleted, or (2) <em><ins>false</ins></em> if not.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to modify data.
	 */
	public boolean removeCompany(Company company) throws UnexpectedError;

	/**
	 * Updates a {@link Company}'s information in database.
	 * 
	 * @param company
	 *            the company
	 * @return either (1) returns <em><ins>true</ins></em> whether an update
	 *         <em><strong>could</strong> be made</em></strong> to a record, or (2)
	 *         <em><ins>false</ins></em> if the update <em>could
	 *         <strong>NOT</strong> be made</em> (no record exists).
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to modify data.
	 */
	public boolean updateCompany(Company company) throws UnexpectedError;

	/**
	 * Gets a {@link Company} from the database by id.<br>
	 * <em>(<strong>including</strong> the coupons linked to him)</em>
	 * 
	 * @param id
	 *            the id
	 * @return returns the {@link Company} or null if {@link Company} does not
	 *         exist.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public Company getCompany(long id) throws UnexpectedError;

	/**
	 * Gets a {@link Company} from the database by id.<br>
	 * <em>(<strong>NOT</strong> including the coupons linked to him)</em>
	 * 
	 * @param id
	 *            the id
	 * @return returns the {@link Company} or null if {@link Company} does not
	 *         exist.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public Company getCompanyOnly(long id) throws UnexpectedError;

	/**
	 * Gets a list of all {@link Company}(ies) from the database.
	 * 
	 * @return returns a list of all companies or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Company> getAllCompanies() throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Company}'s {@link Coupon}s that fit at least
	 * one of the types from the database.
	 * 
	 * @param types
	 *            the types
	 * @param company
	 *            the company
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getCoupons(CouponType[] types, Company company) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Company}'s {@link Coupon}s that fit at least
	 * one of the types and have a price lower than the specified price from the
	 * database.
	 * 
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
	public ArrayList<Coupon> getCoupons(CouponType[] types, double maxPrice, Company company) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Company}'s {@link Coupon}s that have price
	 * lower than the specified price from the database.
	 * 
	 * 
	 * @param maxPrice
	 *            the max price
	 * @param company
	 *            the company
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getCoupons(double maxPrice, Company company) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Company}'s {@link Coupon}s type from the
	 * database.
	 * 
	 * @param company
	 *            the company
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getCoupons(Company company) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Company}'s {@link Coupon}s of the specified
	 * type from the database.
	 * 
	 * 
	 * @param type
	 *            the type
	 * @param company
	 *            the company
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getCouponsByType(CouponType type, Company company) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Company}'s {@link Coupon}s of the specified
	 * type that have a price lower than the specified price from the database.
	 * 
	 * @param type
	 *            the type
	 * @param maxPrice
	 *            the max price
	 * @param company
	 *            the company
	 * @return the list of coupons or null if empty.
	 */
	public ArrayList<Coupon> getCouponsByType(CouponType type, double maxPrice, Company company) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Company}'s archived {@link Coupon}s that fit
	 * at least one of the types from the database.
	 * 
	 * @param types
	 *            the types
	 * @param company
	 *            the company
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getArchivedCoupons(CouponType[] types, Company company) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Company}'s archived {@link Coupon}s that fit
	 * at least one of the types and have a price lower than the specified price
	 * from the database.
	 * 
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
	public ArrayList<Coupon> getArchivedCoupons(CouponType[] types, double maxPrice, Company company)
			throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Company}'s archived {@link Coupon}s that
	 * have price lower than the specified price from the database.
	 * 
	 * 
	 * @param maxPrice
	 *            the max price
	 * @param company
	 *            the company
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getArchivedCoupons(double maxPrice, Company company) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Company}'s archived {@link Coupon}s from the
	 * database.
	 * 
	 * @param company
	 *            the company
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getArchivedCoupons(Company company) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Company}'s archived {@link Coupon}s of the
	 * specified type from the database.
	 * 
	 * 
	 * @param type
	 *            the type
	 * @param company
	 *            the company
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getArchivedCouponsByType(CouponType type, Company company) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Company}'s archived {@link Coupon}s of the
	 * specified type that have a price lower than the specified price from the
	 * database.
	 * 
	 * @param type
	 *            the type
	 * @param maxPrice
	 *            the max price
	 * @param company
	 *            the company
	 * @return the list of coupons or null if empty.
	 */
	public ArrayList<Coupon> getArchivedCouponsByType(CouponType type, double maxPrice, Company company)
			throws UnexpectedError;

	/**
	 * Gets the {@link Company} from the database by matching the login information
	 * with the database. Checks the login information against the database and
	 * returns this {@link Company}.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return returns the company or null if the login information doesn't match.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public Company login(String compName, String password) throws UnexpectedError;

	/**
	 * A quick method to detect if a {@link Company} exists in the database.
	 * 
	 * 
	 * @param company
	 *            the company
	 * @return either (1) returns <em><ins>true</ins></em> if the company exists, or
	 *         (2) <em><ins>false</ins></em> if not.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public boolean doesCompanyExist(Company company) throws UnexpectedError;
}
