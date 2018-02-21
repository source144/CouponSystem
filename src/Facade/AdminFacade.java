package Facade;

import java.util.ArrayList;

import Bean.Company;
import Bean.Coupon;
import Bean.Customer;
import DB.CompanyDBDAO;
import DB.CustomerDBDAO;
import Exceptions.ItemType;
import Exceptions.MessageType;
import Exceptions.AlreadyExists.CompanyAlreadyExists;
import Exceptions.AlreadyExists.CustomerAlreadyExists;
import Exceptions.AlreadyOwns.CompanyAlreadyOwnsCoupon;
import Exceptions.General.BadUsernamePassword;
import Exceptions.General.InvalidSession;
import Exceptions.General.UnexpectedError;
import Exceptions.ListEmpty.EmptyItemList;
import Exceptions.NotFound.CompanyNotFound;
import Exceptions.NotFound.CouponNotFound;
import Exceptions.NotFound.CustomerNotFound;

/**
 * The Administrator facade.<br>
 * <br>
 * Contains method to create, update, remove and get {@link Company} and
 * {@link Customer} in the system. <br>
 * Requires a login method to get instance.<br>
 * <em>All methods <strong>require</strong> a valid instance/session.</em>
 * 
 * @author Gonen Matias
 * @version 1.0 30/01/2018
 *
 */
public final class AdminFacade implements CouponClientFacade {
	private static final CompanyDBDAO companyUtil = new CompanyDBDAO();
	private static final CustomerDBDAO customerUtil = new CustomerDBDAO();
	private static final String USERNAME = "admin", PASSWORD = "admin";
	/**
	 * this instance's session
	 */
	private boolean hasSession = false;

	/**
	 * Constructs a new {@link AdminFacade} and validates session
	 */
	private AdminFacade() {
		this.hasSession = true;
	}

	/**
	 * Gets a valid 'session' of {@link AdminFacade} by logging in.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return returns an new instance of AdminFacade if the username and password
	 *         are correct
	 * @throws BadUsernamePassword
	 *             throws a {@link BadUsernamePassword} exception if username or
	 *             password are incorrect
	 */
	public static AdminFacade login(String username, String password) throws BadUsernamePassword {
		if (username.equals(USERNAME) && password.equals(PASSWORD))
			return new AdminFacade();
		else
			throw new BadUsernamePassword();
	}

	/**
	 * Creates a {@link Company}
	 * 
	 * @param company
	 *            the company
	 * @throws CompanyAlreadyExists
	 *             throws {@link CompanyAlreadyExists} if the ID key or company name
	 *             is already exists.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public void createCompany(Company company) throws CompanyAlreadyExists, UnexpectedError, InvalidSession {
		if (!hasSession)
			throw new InvalidSession(MessageType.ERR, ClientType.ADMIN);

		if (!companyUtil.createCompany(company))
			throw new CompanyAlreadyExists(MessageType.ERR, company.getName(), company.getId());
	}

	/**
	 * Removes a {@link Company}
	 * 
	 * @param company
	 *            the company
	 * @throws CompanyNotFound
	 *             throws {@link CompanyNotFound} if the Company doesn't exist.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public void removeCompany(Company company) throws CompanyNotFound, UnexpectedError, InvalidSession {
		if (!hasSession)
			throw new InvalidSession(MessageType.ERR, ClientType.ADMIN);

		if (!companyUtil.removeCompany(company))
			throw new CompanyNotFound(MessageType.ERR, "", company.getId());
	}

	/**
	 * Updates a {@link Company}
	 * 
	 * @param company
	 *            the company
	 * @throws CompanyNotFound
	 *             throws {@link CompanyNotFound} if the Company doesn't exist.
	 * @throws CompanyAlreadyExists
	 *             throws {@link CompanyAlreadyExists} if the company's name is
	 *             already taken.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public void updateCompany(Company company)
			throws CompanyNotFound, CompanyAlreadyExists, UnexpectedError, InvalidSession {
		if (!hasSession)
			throw new InvalidSession(MessageType.ERR, ClientType.ADMIN);
		if (!companyUtil.updateCompany(company)) {
			if (!companyUtil.doesCompanyExist(company))
				throw new CompanyNotFound(MessageType.ERR, "", company.getId());
			else
				throw new CompanyAlreadyExists(MessageType.ERR, company.getName(), -1);
		}
	}

	/**
	 * Gets a list of all Companies
	 * 
	 * @return a list of all Companies
	 * @throws EmptyItemList
	 *             throws {@link EmptyItemList} if there are no Companies.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public ArrayList<Company> getAllCompanies() throws EmptyItemList, UnexpectedError, InvalidSession {
		if (!hasSession)
			throw new InvalidSession(MessageType.ERR, ClientType.ADMIN);

		ArrayList<Company> companies = companyUtil.getAllCompanies();
		if (companies == null)
			throw new EmptyItemList(MessageType.ERR, ItemType.COMPANY);
		else
			return companies;
	}

	/**
	 * Gets a specific {@link Company} by ID
	 * 
	 * @param id
	 *            the id
	 * @return the Company
	 * @throws CompanyNotFound
	 *             throws {@link CompanyNotFound} if the Company doesn't exist.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public Company getCompany(long id) throws CompanyNotFound, UnexpectedError, InvalidSession {
		if (!hasSession)
			throw new InvalidSession(MessageType.ERR, ClientType.ADMIN);

		Company company = companyUtil.getCompany(id);
		if (company == null)
			throw new CompanyNotFound(MessageType.ERR, "", id);
		else
			return company;
	}

	/**
	 * Creates a {@link Customer}
	 * 
	 * @param customer
	 *            the customer
	 * @throws CustomerAlreadyExists
	 *             throws {@link CustomerAlreadyExists} if the ID key or customer
	 *             name of already exists.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public void createCustomer(Customer customer) throws CustomerAlreadyExists, UnexpectedError, InvalidSession {
		if (!hasSession)
			throw new InvalidSession(MessageType.ERR, ClientType.ADMIN);

		if (!customerUtil.createCustomer(customer))
			throw new CustomerAlreadyExists(MessageType.ERR, customer.getName(), customer.getId());
	}

	/**
	 * Removes a {@link Customer}
	 * 
	 * @param customer
	 *            the customer
	 * @throws CustomerNotFound
	 *             throws {@link CustomerNotFound} if the Customer doesn't exist.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public void removeCustomer(Customer customer) throws CustomerNotFound, UnexpectedError, InvalidSession {
		if (!hasSession)
			throw new InvalidSession(MessageType.ERR, ClientType.ADMIN);

		if (!customerUtil.removeCustomer(customer))
			throw new CustomerNotFound(MessageType.ERR, "", customer.getId());
	}

	/**
	 * Updates a {@link Customer}
	 * 
	 * @param customer
	 *            the customer
	 * @throws CustomerNotFound
	 *             throws {@link CustomerNotFound} if the Customer doesn't exist.
	 * @throws CustomerAlreadyExists
	 *             throws {@link CustomerAlreadyExists} if the customer's name is
	 *             already taken.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public void updateCustomer(Customer customer)
			throws CustomerNotFound, CustomerAlreadyExists, UnexpectedError, InvalidSession {
		if (!hasSession)
			throw new InvalidSession(MessageType.ERR, ClientType.ADMIN);

		if (!customerUtil.updateCustomer(customer)) {
			if (!customerUtil.doesCustomerExist(customer))
				throw new CustomerNotFound(MessageType.ERR, "", customer.getId());
			else
				throw new CustomerAlreadyExists(MessageType.ERR, customer.getName(), -1);
		}
	}

	/**
	 * Gets a list of all Customers
	 * 
	 * @return a list of all Customers
	 * @throws EmptyItemList
	 *             throws {@link EmptyItemList} if there are no Customers.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public ArrayList<Customer> getAllCustomers() throws EmptyItemList, UnexpectedError, InvalidSession {
		if (!hasSession)
			throw new InvalidSession(MessageType.ERR, ClientType.ADMIN);

		ArrayList<Customer> customers = customerUtil.getAllCustomers();
		if (customers == null)
			throw new EmptyItemList(MessageType.ERR, ItemType.CUSTOMER);
		else
			return customers;
	}

	/**
	 * Gets a specific {@link Customer} by ID
	 * 
	 * @param id
	 *            the id
	 * @return the Customer
	 * @throws CustomerNotFound
	 *             throws {@link CustomerNotFound} if the Customer doesn't exist.
	 * @throws UnexpectedError
	 *             throws {@link UnexpectedError} if an unexpected error occurs.
	 * @throws InvalidSession
	 *             throws {@link InvalidSession} if the session of this instance of
	 *             AdminFacade is invalid.
	 */
	public Customer getCustomer(long id) throws CustomerNotFound, UnexpectedError, InvalidSession {
		if (!hasSession)
			throw new InvalidSession(MessageType.ERR, ClientType.ADMIN);

		Customer customer = customerUtil.getCustomer(id);
		if (customer == null)
			throw new CustomerNotFound(MessageType.ERR, "", id);
		else
			return customer;
	}

	// METHODS ::TODO::
	/* [?] Coupon methods?
	 * [?] addCouponToCompany [?] removeCouponFromCompany [?] addCouponToCustomer
	 * [?] removeCouponFromCustomer [?] getCouponsOwnedByCompany ( ) types, max
	 * price ( ) types ( ) max price ( ) type, max price ( ) type ( ) all [?]
	 * getCouponsOwnedByCustomer ( ) types, max price ( ) types ( ) max price ( )
	 * type, max price ( ) type ( ) all [?] archiveCoupon
	 */
}
