package CouponSystem;

import java.sql.Date;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Bean.Coupon;
import DB.CouponDBDAO;
import Exceptions.General.UnexpectedError;
import LogHandler.CouponLogHandler;

/**
 * Daily task to remove and archive old {@link Coupon}s
 * 
 * @author Gonen
 *
 */
public class DailyCouponExpirationTask implements Runnable {

	/**
	 * INTERVAL between tasks.
	 */
	private static final long INTERVAL = 24; // hours
	private static final CouponDBDAO couponUtil = new CouponDBDAO();
	private static final CouponLogHandler handler = CouponLogHandler.getInstance();
	private static boolean isShutdown = false, isRunning = false;
	private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	/**
	 * Constructs a new {@link DailyCouponExpirationTask}.
	 */
	public DailyCouponExpirationTask() {
		
	}

	/**
	 * Fully archives all expired {@link Coupon}s every set hard-coded INTERVAL.
	 */
	@Override
	public void run() {
		executor.scheduleAtFixedRate(() -> {
			try {
				isRunning = true;
				handler.log("NOTE: Daily Coupon Expiration Process started.");

				ArrayList<Coupon> allCoupons = couponUtil.getAllCoupons();

				for (Coupon c : allCoupons) {
					if ((new Date(System.currentTimeMillis()).toLocalDate().isAfter(c.getEndDate().toLocalDate()))) {
						if (couponUtil.addCouponToArchive(c)) {
							if (!couponUtil.removeCoupon(c))
								handler.log("ERR: Unable to delete Coupon for an unknow reason.");
						} else
							handler.log("ERR: Unable to archive expired Coupon for an unknow reason.");
					}
				}

				if (isShutdown)
					handler.log("NOTE: Daily Coupon Expiration Process ended, shutdown successfuly.");
				else
					handler.log("NOTE: Daily Coupon Expiration Process ended.");
				isRunning = false;

			} catch (UnexpectedError e) {
				handler.log(e.getMessage() + "\n" + e.getDetails());
			}
		}, 0, INTERVAL, TimeUnit.HOURS);
	}

	/**
	 * Properly shuts down the archiving processes.
	 */
	public synchronized static void stopTask() {
		if (isRunning) {
			handler.log("NOTE: Daily Coupon Exipiration Thread shutting down.");
			isShutdown = true;
			executor.shutdown();
		}
		else {
			handler.log("NOTE: Daily Coupon Exipiration Thread shutdown successfuly.");
			executor.shutdown();
		}
	}
}
