package LogHandler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//TODO: log(CouponSystemException ex);
//TODO: log(CouponSystemException ex, Scope scope); -> scope is the number of lines of info to log.
//TODO: create ENUM scope, MINIMAL, MEDIUM, DETAILED.
//TODO: log(CouponSystemException ex) runs over all instances of
//	********************************* CouponSystemException and creates a
//	********************************* LogRecord with a message with
//	********************************* the scope specified.
//TODO: add a default SCOPE (final static) to CouponLogHandler;

/**
 * Log handler singleton
 * 
 * @author Gonen Matias
 * @version 1.0 30/01/2018
 */
public class CouponLogHandler {

	private static final String LOG_PATH = "DBDAO_Log", ROOT_PATH = "C:\\Users\\Public\\Logs\\";
	private static final Logger logger = Logger.getLogger("Coupon System DBDAO Log");
	private static final SimpleDateFormat FILE_DATE = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat LOG_DATE = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] \f ");
	private static final boolean logToConsole = true;
	private static FileHandler fileHandler = null;

	/**
	 * Instance holder
	 * 
	 * @author Gonen Matias
	 *
	 */
	private static class Holder {
		private static final CouponLogHandler instance = new CouponLogHandler();
	}

	/**
	 * Constructs the {@link CouponLogHandler}.
	 */
	private CouponLogHandler() {
		boolean success = true;
		String failureDetails = "";
		logger.setUseParentHandlers(false);

		try {
			fileHandler = new FileHandler(ROOT_PATH + LOG_PATH + "_" + FILE_DATE.format(Calendar.getInstance().getTime()) + ".log",
					true);
			fileHandler.setFormatter(new Formatter() {
				@Override
				public String format(LogRecord record) {
					Calendar cal = new GregorianCalendar();
					cal.setTimeInMillis(record.getMillis());
					return LOG_DATE.format(cal.getTime()) + record.getMessage().replaceAll("(\\\r)?\\\n", "\r\n * ")
							+ "\r\n";
				}
			});
		} catch (SecurityException e) {
			success = false;
			failureDetails += "SECURITY EXCEPTION: " + e.getMessage() + "\n";
		} catch (IOException e) {
			success = false;
			failureDetails += "IO EXCEPTION: " + e.getMessage() + "\n";
		}
		if (!success)
			initializationError(failureDetails);
		if (fileHandler != null) {
			logger.addHandler(fileHandler);
			this.log("**********\tLOG CREATED\t**********");
		}
	}

	/**
	 * Gets the {@link CouponLogHandler}'s instance.
	 * 
	 * @return the instance.
	 */
	public static CouponLogHandler getInstance() {
		return Holder.instance;
	}

	/**
	 * Logs a new message to file. <br>
	 * <br>
	 * <em>If <strong>logToConsole</strong> is set to <strong>true</strong>, logs to
	 * console as well</em>.
	 * 
	 * @param message
	 *            the message
	 */
	public void log(String message) {
		logger.log(new LogRecord(Level.OFF, message));
		if (logToConsole) {
			Calendar cal = new GregorianCalendar();
			cal.setTimeInMillis(System.currentTimeMillis());
			System.err.println(LOG_DATE.format(cal.getTime()) + message.replaceAll("(\\\r)?\\\n", "\n * "));
		}
	}

	/**
	 * Logs initialization error and prints to console.
	 * 
	 * @param details
	 *            the error details
	 */
	private static void initializationError(String details) {
		Logger loggerErr = Logger.getLogger(CouponLogHandler.class.getName());
		FileHandler fileHandlerErr = null;
		try {
			fileHandlerErr = new FileHandler(ROOT_PATH + CouponLogHandler.class.getName() + ".log", true);
			fileHandlerErr.setFormatter(new SimpleFormatter());
		} catch (SecurityException | IOException e) {
			System.err.println(LOG_DATE.format(System.currentTimeMillis()) + " Logger Initialization failed.\n * " + details + "\n * " + e.getMessage());
		}
		if (fileHandlerErr  != null) {
			loggerErr.addHandler(fileHandlerErr);
			loggerErr.warning(LOG_DATE.format(System.currentTimeMillis()) + " Logger Initialization failed.\n * " + details + "\n * ");
		}
	}

}