package CouponSystem;

import Exceptions.General.BadUsernamePassword;
import Exceptions.General.UnexpectedError;
import Facade.ClientType;
import Facade.CouponClientFacade;
import LogHandler.CouponLogHandler;

/**
 * The CouponSystem singleton, the mainframe of the program.
 * @author Gonen Matias
 * @version 1.0 02/02/2018
 */
public final class CouponSystem {
	private static final CouponLogHandler handler = CouponLogHandler.getInstance();
	private static Thread dailyExpTask;
	
	/**
	 * Instance holder
	 * 
	 * @author Gonen Matias
	 *
	 */
	private static class Holder {
		private static final CouponSystem instance = new CouponSystem();
	}
	
	/**
	 * Constructs the CouponSystem and runs tasks.
	 */
	private CouponSystem() {
		Thread dailyExpTask = new Thread(new DailyCouponExpirationTask());
		dailyExpTask.start();
	}
	
	/**
	 * Gets the {@link CouponSystem}'s instance.
	 * 
	 * @return the instance.
	 */
	public static CouponSystem getInstance() {
		return Holder.instance;
	}
	
	/**
	 * Attempts to generate a session.
	 * @param username the username
	 * @param password the password
	 * @param clientType the client type
	 * @return returns a generated session (Facade) or null of the username and password were incorrect 
	 *	
	 */// TODO: throw exceptions, what to do with exceptions?
	public CouponClientFacade login(String username, String password, ClientType clientType) /*throws BadUsernamePassword, UnexpectedError */{
		CouponClientFacade facade = null;
		try {
			facade = clientType.login(username, password);
		} catch (BadUsernamePassword e) {
			System.err.println(e.getMessage());
//			throw e;
		} catch (UnexpectedError e) {
			System.err.println(e.getMessage());
			System.err.println(e.getDetails());
			handler.log(e.getMessage() + ".\n"+e.getDetails());
//			throw e;
		}
		return facade; // for now can be null.
	}
	
	/**
	 * Shuts down tasks
	 */
	public void shutdown() {
		
			DailyCouponExpirationTask.stopTask();
	}
}
