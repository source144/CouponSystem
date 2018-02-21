package Facade;

import Exceptions.General.BadUsernamePassword;
import Exceptions.General.UnexpectedError;

/**
 * An ENUM of all client types
 * @author Gonen
 *
 */
public enum ClientType {
	ADMIN {
		/**
		 * Logs into ADMINISTRATOR and returns a new instance of AdminFacade if the login information is correct.
		 * @param username the COMPANY username
		 * @param password the COMPANY password 
		 * @return if login successful, returns this Company's CompanyFacade of the user.
		 * @throws BadUsernamePassword throws {@link BadUsernamePassword} in case of incorrect username or password
		 * @throws UnexpectedError throws {@link UnexpectedError} in case of an unexpected error
		 */
		@Override
		public CouponClientFacade login(String username, String password) throws BadUsernamePassword, UnexpectedError {
			return AdminFacade.login(username, password);
		}
	}, COMPANY {
		/**
		 * Logs into COMPANY user and returns a new instance of CompanyFacade for this user if the login information is correct.
		 * @param username the COMPANY username
		 * @param password the COMPANY password 
		 * @return if login successful, returns this Company's CompanyFacade of the user.
		 * @throws BadUsernamePassword throws {@link BadUsernamePassword} in case of incorrect username or password
		 * @throws UnexpectedError throws {@link UnexpectedError} in case of an unexpected error
		 */
		@Override
		public CouponClientFacade login(String username, String password) throws BadUsernamePassword, UnexpectedError {
			return CompanyFacade.login(username, password);
		}
		
	}, CUSTOMER {
		/**
		 * Logs into CUSTOMER user and returns a new new instance CustomerFacade for this user if the login information is correct.
		 * @param username the CUSTOMER username
		 * @param password the CUSTOMER password 
		 * @return if login successful, returns this Customer's CustomerFacade of the user.
		 * @throws BadUsernamePassword throws {@link BadUsernamePassword} in case of incorrect username or password
		 * @throws UnexpectedError throws {@link UnexpectedError} in case of an unexpected error
		 */
		@Override
		public CouponClientFacade login(String username, String password) throws BadUsernamePassword, UnexpectedError {
			return CustomerFacade.login(username, password);
		}
	};
	
	/**
	 * A method that logs into to a user and returns a new instance of his facade if the login information is correct.
	 * @param username the username
	 * @param password the password 
	 * @return if login successful, returns the facade of the user.
	 * @throws BadUsernamePassword throws {@link BadUsernamePassword} in case of incorrect username or password
	 * @throws UnexpectedError throws {@link UnexpectedError} in case of an unexpected error
	 */
	public abstract CouponClientFacade login(String username, String password) throws BadUsernamePassword, UnexpectedError ;
}
