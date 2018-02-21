import java.sql.Date;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import Bean.Company;
import Bean.Coupon;
import Bean.CouponType;
import Bean.Customer;
import CouponSystem.CouponSystem;
import DB.CouponDBDAO;
import Exceptions.AlreadyExists.CouponAlreadyExists;
import Exceptions.AlreadyExists.CustomerAlreadyExists;
import Exceptions.DoesntOwn.CompanyDoesntOwnCoupon;
import Exceptions.General.InvalidSession;
import Exceptions.General.UnexpectedError;
import Exceptions.ListEmpty.EmptyFilteredList;
import Exceptions.ListEmpty.EmptyItemList;
import Exceptions.NotFound.CompanyNotFound;
import Exceptions.NotFound.CouponNotFound;
import Exceptions.NotFound.CustomerNotFound;
import Facade.AdminFacade;
import Facade.ClientType;
import Facade.CompanyFacade;
import Facade.CouponClientFacade;
import Facade.CustomerFacade;
import LogHandler.CouponLogHandler;

public class main {

	/*
	 * // TODO: // // ** Make COMP_NAME, CUST_NAME, COUP_NAME as UNIQUE! // **
	 * Duplicate values return false, and throw AlreadyExist. // ** ?? Can't update
	 * COMP_NAME, CUST_NAME, COUP_NAME // // [ ] Make JAVA DOCS // { } CompanyDBDAO
	 * // ( ) createCompany(Company company) // ( ) removeCompany(Company company)
	 * // ( ) updateCompany(Company company) // ( ) getCompany(long id) // ( )
	 * getAllCompanies() // ( ) getCoupons(Company company) // ( ) login(String
	 * compName, String password) // { } CouponDBDAO // ( ) createCoupon(Coupon
	 * coupon) // ( ) createCoupon(Coupon coupon, Company company) // ( )
	 * removeCoupon(Coupon coupon) // ( ) updateCoupon(Coupon coupon) // ( )
	 * getCoupon(long id) // ( ) getAllCoupons() // ( ) getCouponsByType(CouponType
	 * type) // ( ) getCouponsByType(CouponType[] types) // ( )
	 * addCouponToCompany(Coupon coupon, Company company) // ( )
	 * addCouponToCustomer(Coupon coupon, Customer customer) // ( )
	 * removeCouponFromCompany(Coupon coupon, Company company) // ( )
	 * removeCouponFromCustomer(Coupon coupon, Customer customer) // { }
	 * CustomerDBDAO // ( ) createCustomer(Customer customer) // ( )
	 * removeCustomer(Customer customer) // ( ) updateCustomer(Customer customer) //
	 * ( ) getCustomer(long id) // ( ) getAllCustomers() // ( ) getCoupons(Customer
	 * customer) // ( ) login(String custName, String password) // { }
	 * CouponLogHandler // ( ) getInstance() // ( ) log(String message) // ( )
	 * CouponLogHandler() // { } Coupon (BEAN) // { } Company (BEAN) // { } Customer
	 * (BEAN) // // // ************** IN DBDAO(s) ************* // Make new class //
	 * [ ] SQLUtils or DBUtils // - Include in it final static variable: // *
	 * tbl_coupon // * tbl_company // * tbl_customer // * tbl_join_customer // *
	 * tbl_join_company // * FK_COMPANY_ID // * FK_CUSTOMER_ID // * FK_COUPON_ID //
	 * * getResult // - Include in it static final functions: // * runStatement(...)
	 * // * getResult(...) // ************** IN DBDAO(s) ************* // Make every
	 * throw statement include information about: // ID requested.. // table
	 * requested.. // like in createCompany, createCustomer, etc... //
	 * ************** IN DBDAO(s) ************* // // ************** IN EXCEPTION(s)
	 * ************* // [V] CREATE RelationshipAlreadyExists // * table1, // *
	 * table2, // * id1, // * id2, // ** Maybe add enums for ItemType (item1, item2)
	 * // // [V] CREATE RelationshipNotFound // * table1, // * table2, // * id1, //
	 * * id2, // ** Maybe add enums for ItemType (item1, item2) // // [ ] CREATE
	 * Enum RelationshipType // * CompanyCoupon // * CustomerCoupon // //
	 * ************** IN FACADE(s) ************* // [V] Create ClientFacade
	 * interface // (V) login(String username, String password, [ENUM]ClientType
	 * clientType) // {V} AdminFacade // (V) createCompany(Company company) // (V)
	 * removeCompany(Company company) // (V) updateCompany(Company company) // (V)
	 * getAllCompanies() // (V) getCompany(long id) // (V) createCustomer(Customer
	 * customer) // (V) removeCustomer(Customer customer) // (V)
	 * updateCustomer(Customer customer) // (V) getAllCustomers() // (V)
	 * getCustomer(long id) // {V} CompanyFacade // - Company thisCompany // (V)
	 * createCoupon(Coupon coupon) // (V) removeCoupon(Coupon coupon) // (V)
	 * updateCoupon(Coupon coupon) // (V) getCompanyInfo() // (V) getCoupons() //
	 * (V) getCouponsBy(CouponType[] type, double maxPrice, Date endDate) // (V)
	 * getCouponsByType(CouponType type) // (V) getCouponsByType(CouponType[] type)
	 * // (V) getCouponsByMaxPrice(double maxPrice) // (V)
	 * getCouponsByDateRange(Date endDate) // {V} CustomerFacade // (V)
	 * buyCoupon(Coupon coupon) // - CHECK AMOUNT // - CHECK DATE // -
	 * addCouponToCustomer returns false: Customer Already Owns Coupon // (V)
	 * getPurchaseHistory() // (V) getPurchaseHistoryBy(CouponType[] type) // (V)
	 * getPurchaseHistoryBy(CouponType type) // (V)
	 * getPurchaseHistoryBy(CouponType[] type, double maxPrice) // (V)
	 * getPurchaseHistoryBy(CouponType type, double maxPrice) // (V)
	 * getPurchaseHistoryBy(double maxPrice) // // [ ] HANDLE ALL EXCEPTIONS IN
	 * FACADE // {V} CouponNotFound // {V} CustomerNotFound // {V} CompanyNotFound
	 * // { } RelationshipNotFound // {V} CouponAlreadyExists // {V}
	 * CustomerAlreadyExists // {V} CompanyAlreadyExists // { }
	 * RelationshipAlreadyExists // {V} EmptyFilteredList // {V} EmptyItemList //
	 * {V} BadUsernamePassword // ************** IN FACADE(s) ************* // //
	 * DEBUG METHODS OF CustomerDBDAO [ ] // [ ] Get Company for EXISITNG Company
	 * with Coupon Relationship(s) // // DEBUG ALL METHODS OF CustomerDBDAO [ ] // [
	 * ] Create Customer for EXISTING Customer // [ ] Create Customer for
	 * NON-EXISTING Customer // [ ] // [ ] Remove Customer for EXISTING Customer //
	 * [ ] Remove Customer for EXISTING Customer with VALID Coupon Relationship // [
	 * ] Remove Customer for EXISTING Customer with INVALID Coupon Relationship // [
	 * ] Remove Customer for NON-EXISTING Customer // [ ] // [ ] Update Customer for
	 * EXISTING Customer // [ ] Update Customer for NON-EXISTING Customer // [ ] //
	 * [ ] Get Customer for EXISITNG Customer // [ ] Get Customer for EXISITNG
	 * Customer with Coupon Relationship(s) // [ ] Get Customer for NON-EXISITNG
	 * Customer // [ ] // [ ] Get ALL Customers // [ ] // [ ] Login with VALID info
	 * // [ ] Login with VALID userName and INVALID password // [ ] Login with
	 * INVALID userName and VALID password // [ ] Login with INVALID info // //
	 * DEBUG ALL METHODS OF CouponDBDAO [V] // [V] Create Coupon for EXISTING
	 * Coupon. // [V] Create Coupon for NON-EXISTING Coupon. // [V] Create Coupon
	 * (with Company) for EXISTING Coupon with NON-EXISTING Company. // [V] Create
	 * Coupon (with Company) for EXISTING Coupon with EXISTING Company. // [V]
	 * Create Coupon (with Company) for NON-EXISTING Coupon with NON-EXISTING
	 * Company. // [V] Create Coupon (with Company) for NON-EXISTING Coupon with
	 * EXISTING Company. // [V] Remove Coupon for EXISTING Coupon // [V] Remove
	 * Coupon for EXISTING Coupon with Company Relationship // [V] Remove Coupon for
	 * EXISTING Coupon with Customer Relationship // [V] Remove Coupon for EXISTING
	 * Coupon with Company AND Customer Relationship // [V] Remove Coupon for
	 * NON-EXISTING Coupon // [V] Update Coupon for EXISTING Coupon // [V] Update
	 * Coupon for NON-EXISTING Coupon // [V] Get Coupon for EXISITNG Coupon // [V]
	 * Get Coupon for NON-EXISITNG Coupon // [V] Get ALL Coupons // [V] Get Coupons
	 * by Type // [V] Get Coupons by Type Array // [V] Add EXISTING Coupon to
	 * EXISTING Company - false: RelationShipAlreadyExists // [V] Add NON-EXISTING
	 * Coupon to EXISTING Company // [V] Add EXISTING Coupon to NON-EXISTING Company
	 * // [V] Add NON-EXISTING Coupon to NON-EXISTING Company // // [V] Remove
	 * EXISTING Coupon from EXISTING Company // [V] Remove NON-EXISTING Coupon from
	 * EXISTING Company - false: RelationshipNotFound // [V] Remove EXISTING Coupon
	 * from NON-EXISTING Company - false: RelationshipNotFound // [V] Remove
	 * NON-EXISTING Coupon from NON-EXISTING Company - false: RelationshipNotFound
	 * // // [V] Add EXISTING Coupon to EXISTING Customer - false:
	 * RelationShipAlreadyExists // [V] Add NON-EXISTING Coupon to EXISTING Customer
	 * // [V] Add EXISTING Coupon to NON-EXISTING Customer // [V] Add NON-EXISTING
	 * Coupon to NON-EXISTING Customer // // [V] Remove EXISTING Coupon from
	 * EXISTING Customer // [V] Remove NON-EXISTING Coupon from EXISTING Customer -
	 * false: RelationshipNotFound // [V] Remove EXISTING Coupon from NON-EXISTING
	 * Customer - false: RelationshipNotFound // [V] Remove NON-EXISTING Coupon from
	 * NON-EXISTING Customer - false: RelationshipNotFound
	 */

	static CouponDBDAO db = new CouponDBDAO();
	static Date dt = new Date(System.currentTimeMillis());
	static Coupon testCoupon = new Coupon(332, "GOnen", "message", "image", dt, dt, 15, CouponType.HOTELS, 32.5);
	static Coupon testCoupon2 = new Coupon(666, "noah", "message1212", "image123123", dt, dt, 20, CouponType.HEALTH,
			66.5);
	static Coupon testCoupon3 = new Coupon(34, "judy", "message", "image", dt, dt, 10, CouponType.CAMPING, 20.1);
	static Company testCompany = new Company("Judy", "jayjur1112", "jayjur@gmail.com", 123545);
	static Company testCompany2 = new Company("Judy", "jayjur", "jayjur@gmail.com", 20);
	static Company testCompany3 = new Company("Judy", "jayjur", "jayjur@gmail.com", 22);
	static Company testCompany4 = new Company("Judy", "jayjur", "jayjur@gmail.com", 23);
	static Customer testCustomer1 = new Customer("Yoel", "1234567777", 56);
	static Customer testCustomer2 = new Customer("Yoel", "newPassword", 56);
	static Customer testCustomer3 = new Customer("Gonen", "1234567777", 3);
	static CouponSystem cSys;
	static CouponLogHandler handler = CouponLogHandler.getInstance();
	static CouponClientFacade mainFacade;
	static CouponSystem mainSys;
	static Scanner in = new Scanner(System.in);
	static CouponDBDAO couponUtil = new CouponDBDAO();

	public static void main(String[] args) {
		cLog("MAIN STARTED\n\n");
		cLog("Coupons in database");
		try {
			System.out.println(couponUtil.getAllCoupons().size());
			for (Coupon c : couponUtil.getAllCoupons())
				System.out.println(c);
		} catch (UnexpectedError e) {
			handler.log(e.getDetails());
		} finally { System.out.println("\n\n"); }

		cLog("Archived coupons in database");
		try {
			System.out.println(couponUtil.getAllArchivedCoupons().size());
			for (Coupon c : couponUtil.getAllArchivedCoupons())
				System.out.println(c);
		} catch (UnexpectedError e) {
			handler.log(e.getDetails());
		} finally { System.out.println("\n\n"); }
		
		cLog("Running system now");
		System.out.println("Enter username..");
		String username = in.next();
		System.out.println("Enter password..");
		String password = in.next();
		
		cSys = CouponSystem.getInstance();
		CouponClientFacade facade = cSys.login(username, password, ClientType.COMPANY);
		if (facade != null) {
			if (facade instanceof CompanyFacade) {
				System.out.println("COMPANY LOGGED IN");
				try {
					System.out.println(((CompanyFacade) facade).getCompany());
				} catch (InvalidSession | UnexpectedError e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
				
				System.out.println("Enter anything to see the coupons you own.");
				in.next();
				try {
					for (Coupon c : ((CompanyFacade) facade).getCoupons())
						System.out.println(c);
				} catch (EmptyFilteredList | UnexpectedError | InvalidSession e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
				
				
				try {
					System.out.println("Add new coupon...");
//					testCoupon = createCoupon();
					testCoupon = new Coupon(123, "Medicare4U", "message1212", "image123123", dt, dt, 20, CouponType.HEALTH,
							66.5);
					((CompanyFacade) facade).createCoupon(testCoupon);
				} catch (CouponAlreadyExists | UnexpectedError | InvalidSession e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
				
				try {
					System.out.println("Add new coupon...");
//					testCoupon = createCoupon();
					testCoupon = new Coupon(123, "Medicare4U", "message1212", "image123123", dt, dt, 20, CouponType.HEALTH,
							66.5);
					((CompanyFacade) facade).createCoupon(testCoupon);
				} catch (CouponAlreadyExists | UnexpectedError | InvalidSession e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
				
				System.out.println("Enter anything to see the coupons you own.");
				in.next();
				try {
					for (Coupon c : ((CompanyFacade) facade).getCoupons())
						System.out.println(c);
				} catch (EmptyFilteredList | UnexpectedError | InvalidSession e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
				
//				System.out.println("update title for: "+testCoupon);
				try {
//					String title = in.next();
//					testCoupon.setTitle(title);
//					System.out.println("Update type");
//					String t = in.next();
//					testCoupon.setType(CouponType.getEnum(t));

					testCoupon2 = new Coupon(123, "UPDATED Medicare4U", "message1212", "image123123", dt, dt, 20, CouponType.RESTAURANTS,
							66.5);
					((CompanyFacade) facade).updateCoupon(testCoupon2);
				} catch (CouponAlreadyExists | UnexpectedError | InvalidSession | CouponNotFound | CompanyDoesntOwnCoupon e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
				System.out.println("Enter anything to see the coupons you own.");
				in.next();
				try {
					for (Coupon c : ((CompanyFacade) facade).getCoupons())
						System.out.println(c);
				} catch (EmptyFilteredList | UnexpectedError | InvalidSession e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
			}
			if (facade instanceof CustomerFacade) {
				System.out.println("CUSTOMER LOGGED IN");
				
			}
			if (facade instanceof AdminFacade) {
				System.out.println("ADMIN LOGGED IN");
				try {
					try {
						for (Company c : ((AdminFacade) facade).getAllCompanies())
							System.out.println(c);
					} catch (UnexpectedError | InvalidSession | EmptyItemList  e) {
						System.err.println(e.getMessage() + "\n" + e.getDetails());
					}
					
					System.out.println("\n\n\n");
//					((AdminFacade) facade).createCompany(testCompany);
//				} catch (UnexpectedError | InvalidSession | CompanyAlreadyExists e) {
//					System.err.println(e.getMessage() + "\n" + e.getDetails());
//				}
					((AdminFacade) facade).removeCompany(testCompany);
				} catch (UnexpectedError | InvalidSession | CompanyNotFound e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
//					((AdminFacade) facade).updateCompany(testCompany);
//				} catch (UnexpectedError | InvalidSession | CompanyNotFound | CompanyAlreadyExists e) {
//					System.err.println(e.getMessage() + "\n" + e.getDetails());
//				}
				try {
					for (Company c : ((AdminFacade) facade).getAllCompanies())
						System.out.println(c);
				} catch (UnexpectedError | InvalidSession | EmptyItemList  e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
				
				try {
					for (Customer c : ((AdminFacade) facade).getAllCustomers())
						System.out.println(c);
				} catch (UnexpectedError | InvalidSession | EmptyItemList  e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
				
				
				try {
					testCustomer1 = ((AdminFacade) facade).getCustomer(testCustomer1.getId());
				} catch (UnexpectedError | InvalidSession | CustomerNotFound  e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
				
				try {
					for (Customer c : ((AdminFacade) facade).getAllCustomers())
						System.out.println(c);
				} catch (UnexpectedError | InvalidSession | EmptyItemList  e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
				
				
				System.out.println("\nenter new name for customer\n"+testCustomer1);
				testCustomer1.setName(in.next());
				try {
					((AdminFacade) facade).updateCustomer(testCustomer1);
				} catch (UnexpectedError | InvalidSession | CustomerAlreadyExists | CustomerNotFound e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
				in.next();
				try {
					System.out.println(((AdminFacade) facade).getCustomer(11));
				} catch (UnexpectedError | InvalidSession | CustomerNotFound  e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
				in.next();
				testCustomer2 = new Customer("Yoe12121", "hi_there", 3333);
				try {
					((AdminFacade) facade).removeCustomer(testCustomer2);
				} catch (UnexpectedError | InvalidSession | CustomerNotFound  e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
				try {
					for (Customer c : ((AdminFacade) facade).getAllCustomers())
						System.out.println(c);
				} catch (UnexpectedError | InvalidSession | EmptyItemList  e) {
					System.err.println(e.getMessage() + "\n" + e.getDetails());
				}
				
//					System.out.println(((AdminFacade) facade).getCompany(12));
//					} catch (UnexpectedError | InvalidSession | CompanyNotFound e) {
//					System.err.println(e.getMessage() + "\n" + e.getDetails());
//				}
			}
		}
		else 
			System.out.println("INCORRECT LOGIN INFO");
		cLog("Enter anything to shutdown");
//		in.next();
		cSys.shutdown();
	}

	
	public static void cLog(String msg) {
		System.out.println("***CONSOLE: " + msg);
	}
	
	public static Coupon createCoupon() {
		System.out.println("Enter Coupon type");
		CouponType couponType = CouponType.getEnum(in.next());
		Date start = new Date(System.currentTimeMillis() + randLong(12316789, 987321100.1)),
				end = new Date(start.getTime() + randLong(12341567, 981765430.1));
		return new Coupon(randLong(1, 300), rndStr(), rndStr(), rndStr(), start, end, randInt(1, 30),
				couponType, randDbl(10, 60));
	}
	
	public static void testCouponDBDAO() {
		String debugDetail = Thread.currentThread().getStackTrace()[1].getClassName() + " - "
				+ Thread.currentThread().getStackTrace()[1].getMethodName();
		System.out.println("Enter Coupon type");
		CouponType couponType = CouponType.getEnum(in.nextLine());
		Date start = new Date(System.currentTimeMillis() + randLong(12316789, 987321100.1)),
				end = new Date(start.getTime() + randLong(12341567, 981765430.1));
		Coupon coupon = new Coupon(randLong(1, 300), rndStr(), rndStr(), rndStr(), start, end, randInt(1, 30),
				couponType, randDbl(10, 60));
		try {
			System.out.println("createCoupon");
			System.out.println(couponUtil.createCoupon(coupon));
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("createCouponDB\n" + e.getMessage() + "\n" + e.getDetails());
		}
		
		in.next();
		
		try {
			System.out.println("get Archived coupon");
			Coupon c = couponUtil.getCoupon(true, 268);
			if (c == null)
				System.out.println("null");
			else
				System.out.println(c);
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("createCouponDB\n" + e.getMessage() + "\n" + e.getDetails());
		}
		
		
		try {
			System.out.println("createCoupon");
			System.out.println(couponUtil.createCoupon(coupon));
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("createCouponDB\n" + e.getMessage() + "\n" + e.getDetails());
		}

		try {
			System.out.println("getCoupon");
			System.out.println(couponUtil.getCoupon(coupon.getId()));
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("Store in DB\n" + e.getMessage() + "\n" + e.getDetails());
		}
		
		in.next();
		
		try {
			System.out.println("addCouponToCompany");
			System.out.println(couponUtil.addCouponToCompany(coupon, testCompany));
			System.out.println(couponUtil.addCouponToCompany(coupon, testCompany2));
			System.out.println(couponUtil.addCouponToCompany(coupon, testCompany3));
			System.out.println(couponUtil.addCouponToCompany(coupon, testCompany4));
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("addCouponToArchive\n" + e.getMessage() + "\n" + e.getDetails());
		}
		
		try {
			System.out.println("addCouponToCustomer");
			System.out.println(couponUtil.addCouponToCustomer(coupon, testCustomer2));
			System.out.println(couponUtil.addCouponToCustomer(coupon, testCustomer3));
			System.out.println(couponUtil.addCouponToCustomer(coupon, testCustomer1));
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("addCouponToArchive\n" + e.getMessage() + "\n" + e.getDetails());
		}

		in.next();
		
		try {
			System.out.println("removeCouponFromCompany");
			System.out.println(couponUtil.removeCouponFromCompany(coupon, testCompany));
			System.out.println(couponUtil.removeCouponFromCompany(coupon, testCompany4));
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("removeCouponToCompany\n" + e.getMessage() + "\n" + e.getDetails());
		}
		
		try {
			System.out.println("removeCouponFromCustomer");
			System.out.println(couponUtil.removeCouponFromCustomer(coupon, testCustomer1));
			System.out.println(couponUtil.removeCouponFromCustomer(coupon, testCustomer2));
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("removeCouponToCustomer\n" + e.getMessage() + "\n" + e.getDetails());
		}
		
		in.next();
		
		try {
			System.out.println("addCouponToArchive");
			System.out.println(couponUtil.addCouponToArchive(coupon));
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("addCouponToArchive\n" + e.getMessage() + "\n" + e.getDetails());
		}
		

		in.next();
		
		try {
			System.out.println("getAllArchivedCoupons");
			ArrayList<Coupon> coupons = couponUtil.getAllArchivedCoupons();
			System.out.println(coupons.size() + " Coupons");
			for (Coupon c : coupons)
				System.out.println(c);
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("getAllCoupons\n" + e.getMessage() + "\n" + e.getDetails());
		}
		
		try {
			System.out.println("createCoupon");
			System.out.println(couponUtil.createCoupon(coupon));
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("createCouponDB\n" + e.getMessage() + "\n" + e.getDetails());
		}

		try {
			System.out.println("getCoupon");
			System.out.println(couponUtil.getCoupon(coupon.getId()));
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("Store in DB\n" + e.getMessage() + "\n" + e.getDetails());
		}

		try {
			System.out.println("updateCoupon");
			coupon.setAmount(12);
			coupon.setType(couponType.FOOD);
			System.out.println(couponUtil.updateCoupon(coupon));
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("Store in DB\n" + e.getMessage() + "\n" + e.getDetails());
		}

		try {
			System.out.println("getCoupon");
			System.out.println(couponUtil.getCoupon(coupon.getId()));
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("Store in DB\n" + e.getMessage() + "\n" + e.getDetails());
		}

		try {
			double maxPrice = randDbl(1, 100);
			System.out.println("getCoupons maxPrice=" + maxPrice + ", types");
			for (Coupon c : couponUtil.getCoupons(getTypes(), maxPrice))
				System.out.println(c);
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("getCoupons maxPrice, types\n" + e.getMessage() + "\n" + e.getDetails());
		}

		try {
			System.out.println("getCoupons types");
			for (Coupon c : couponUtil.getCoupons(getTypes()))
				System.out.println(c);
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("getCoupons types\n" + e.getMessage() + "\n" + e.getDetails());
		}
		

		try {
			System.out.println("removeCoupon");
			System.out.println(couponUtil.removeCoupon(coupon));
			System.out.println();
		} catch (UnexpectedError e) {
			// TODO Auto-generated catch block
			System.err.println("removeCoupon\n" + e.getMessage() + "\n" + e.getDetails());
		}
		


	}

	public static CouponType[] getTypes() {
		System.out.println("Enter Coupon type to check!");
		ArrayList<CouponType> types = new ArrayList<CouponType>();
		String debugDetail = Thread.currentThread().getStackTrace()[1].getClassName() + " - "
				+ Thread.currentThread().getStackTrace()[1].getMethodName();
		String type = in.nextLine();
		do {
			CouponType couponType = CouponType.getEnum(type);
			System.out.println("You have entered string: " + type);
			System.out.println("Transformed into ENUM: " + couponType.getValue());
			System.out.println("Printable CouponType: " + couponType);
			types.add(couponType);
			System.out.println();
			System.out.println("Enter another Coupon type to check!");
			type = in.nextLine();
		} while (!type.equals("quit"));

		CouponType[] result = new CouponType[types.size()];
		types.toArray(result);
		return result;
		// System.out.println("Closed succesfully.\n" + debugDetail + "\n\n");
	}

	public static String rndStr() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 5) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;

	}

	public static double randDbl(double start, double end) {
		return start + Math.round(Math.random() * (end - start));
	}

	public static int randInt(int start, int end) {
		return start + (int) Math.round(Math.random() * (end - start));
	}

	public static long randLong(double start, double end) {
		return (long) (start + Math.round(Math.random() * (end - start)));
	}
}
