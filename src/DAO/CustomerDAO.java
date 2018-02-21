package DAO;

import java.util.ArrayList;

import Bean.Coupon;
import Bean.CouponType;
import Bean.Customer;
import Exceptions.General.UnexpectedError;

/**
 * 
 * Customer data access object interface
 * 
 * Includes all the the basic actions to be performed on data
 * 
 * @author Gonen Matias
 * @version 1.0 30/01/2018
 */
public interface CustomerDAO {
	/**
	 * Stores a {@link Customer} in database.
	 * 
	 * @param customer
	 *            the customer
	 * @return either (1) returns <em><ins>true</ins></em> if a record has been
	 *         added, or (2) <em><ins>false</ins></em> if not.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to modify data.
	 */
	public boolean createCustomer(Customer customer) throws UnexpectedError;

	/**
	 * removes {@link Customer} and all data relating to it from database.
	 * 
	 * @param customer
	 *            the customer
	 * @return either (1) returns <em><ins>true</ins></em> if a record has been
	 *         deleted, or (2) <em><ins>false</ins></em> if not.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to modify data.
	 */
	public boolean removeCustomer(Customer customer) throws UnexpectedError;

	/**
	 * Updates a {@link Customer}'s information in database.
	 * 
	 * @param customer
	 *            the customer
	 * @return either (1) returns <em><ins>true</ins></em> whether an update
	 *         <em><strong>could</strong> be made</em></strong> to a record, or (2)
	 *         <em><ins>false</ins></em> if the update <em>could
	 *         <strong>NOT</strong> be made</em> (no record exists).
	 * @return either (1) returns <em><ins>true</ins></em> whether update
	 *         <strong><em>could</strong></em> be made or (2)
	 *         <em><ins>false</ins></em> if that update has failed due to a
	 *         duplicate value.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to modify data.
	 */

	public boolean updateCustomer(Customer customer) throws UnexpectedError;

	/**
	 * Gets a {@link Customer} from the database by id.<br>
	 * <em>(<strong>including</strong> the coupons linked to him)</em>
	 * 
	 * @param id
	 *            the id
	 * @return returns the {@link Customer} or null if {@link Customer} does not
	 *         exist.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public Customer getCustomer(long id) throws UnexpectedError;

	/**
	 * Gets a {@link Customer} from the database by id.<br>
	 * <em>(<strong>NOT</strong> including the coupons linked to him)</em>
	 * 
	 * @param id
	 *            the id
	 * @return returns the {@link Customer} or null if {@link Customer} does not
	 *         exist.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public Customer getCustomerOnly(long id) throws UnexpectedError;

	/**
	 * Gets a list of all {@link Customer}s from the database.
	 * 
	 * @return returns a list of all customers or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Customer> getAllCustomers() throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Customer}'s {@link Coupon}s that fit at
	 * least one of the types from the database.
	 * 
	 * @param types
	 *            the types
	 * @param customer
	 *            the customer
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getCoupons(CouponType[] types, Customer customer) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Customer}'s {@link Coupon}s that fit at
	 * least one of the types and have a price lower than the specified price
	 * from the database.
	 * 
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
	public ArrayList<Coupon> getCoupons(CouponType[] types, double maxPrice, Customer customer) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Customer}'s {@link Coupon}s that have price
	 * lower than the specified price from the database.
	 * 
	 * 
	 * @param maxPrice
	 *            the max price
	 * @param customer
	 *            the customer
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getCoupons(double maxPrice, Customer customer) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Customer}'s {@link Coupon}s type from the
	 * database.
	 * 
	 * @param customer
	 *            the customer
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getCoupons(Customer customer) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Customer}'s {@link Coupon}s of the specified
	 * type from the database.
	 * 
	 * 
	 * @param type
	 *            the type
	 * @param customer
	 *            the customer
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getCouponsByType(CouponType type, Customer customer) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Customer}'s {@link Coupon}s of the specified
	 * type that have a price lower than the specified price from the database.
	 * 
	 * @param type
	 *            the type
	 * @param maxPrice
	 *            the max price
	 * @param customer
	 *            the customer
	 * @return the list of coupons or null if empty.
	 */
	public ArrayList<Coupon> getCouponsByType(CouponType type, double maxPrice, Customer customer)
			throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Customer}'s archived {@link Coupon}s that
	 * fit at least one of the types from the database.
	 * 
	 * @param types
	 *            the types
	 * @param customer
	 *            the customer
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getArchivedCoupons(CouponType[] types, Customer customer) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Customer}'s archived {@link Coupon}s that
	 * fit at least one of the types and have a price lower than the specified price
	 * from the database.
	 * 
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
	public ArrayList<Coupon> getArchivedCoupons(CouponType[] types, double maxPrice, Customer customer)
			throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Customer}'s archived {@link Coupon}s that
	 * have price lower than the specified price from the database.
	 * 
	 * 
	 * @param maxPrice
	 *            the max price
	 * @param customer
	 *            the customer
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getArchivedCoupons(double maxPrice, Customer customer) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Customer}'s archived {@link Coupon}s from
	 * the database.
	 * 
	 * @param customer
	 *            the customer
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getArchivedCoupons(Customer customer) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Customer}'s archived {@link Coupon}s of the
	 * specified type from the database.
	 * 
	 * 
	 * @param type
	 *            the type
	 * @param customer
	 *            the customer
	 * @return the list of coupons or null if empty.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public ArrayList<Coupon> getArchivedCouponsByType(CouponType type, Customer customer) throws UnexpectedError;

	/**
	 * Gets a list of a specific {@link Customer}'s archived {@link Coupon}s of the
	 * specified type that have a price lower than the specified price from the
	 * database.
	 * 
	 * @param type
	 *            the type
	 * @param maxPrice
	 *            the max price
	 * @param customer
	 *            the customer
	 * @return the list of coupons or null if empty.
	 */
	public ArrayList<Coupon> getArchivedCouponsByType(CouponType type, double maxPrice, Customer customer)
			throws UnexpectedError;

	/**
	 * Gets the {@link Customer} from the database by matching the login information
	 * with the database. Checks the login information against the database and
	 * returns this {@link Customer}.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return returns the customer or null if the login information doesn't match.
	 * @throws UnexpectedError
	 *             Throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public Customer login(String username, String password) throws UnexpectedError;

	/**
	 * A quick method to detect if a {@link Customer} exists in the database.
	 * 
	 * 
	 * @param customer
	 *            the customer
	 * @return either (1) returns <em><ins>true</ins></em> if the customer exists,
	 *         or (2) <em><ins>false</ins></em> if not.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an error occurred and the
	 *             method was unable to reach data.
	 */
	public boolean doesCustomerExist(Customer customer) throws UnexpectedError;
}