package domain;

public class Constants {
	public static final String STATUS_LOGGED_IN = "LOGGED_IN";
	public static final String STATUS_LOGGED_OUT = "LOGGED_OUT";
	public static final long SESSION_KEY_EXPIRY = 3*24*60*60*1000 ; 
	public static final int SESSION_KEY_USAGE_LIMIT = 15;
	public static final String PHONE_NUMBER = "PHONE_NUMBER";
	public static final int OTP_Length = 6;
	public static final long OTP_LIFESPAN = 300;
	public static final String TRANSACTION_STATUS_INITIATED = "INITIATED";
	public static final String TRANSACTION_STATUS_COMPLETED = "COMPLETED";
	public static final String TRANSACTION_STATUS_FAILED = "FAILED";
	public static final String TRNASACTION_TYPE_AUTH = "AUTH" ;
	public static final String TRNASACTION_TYPE_CASH = "CASH";
	public static final String TRNASACTION_TYPE_PARTIAL = "PARTIAL";
	
	public static final String COLLECTIONS_CUSTOMER = "CUSTOMERS";
	public static final String COLLECTIONS_MERCHANTS = "MERCHANTS";
	public static final String COLLECTIONS_TRANSACTIONS = "TRANSACTIONS";
	public static final String DATABASE_CHANGEPAY = "CHANGEPAY";
}
