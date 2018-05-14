package domain;

public class Constants {
	public static final String STATUS_LOGGED_IN = "LOGGED_IN";
	public static final String STATUS_LOGGED_OUT = "LOGGED_OUT";
	public static final long SESSION_KEY_EXPIRY = 3*24*60*60*1000 ; 
	public static final int SESSION_KEY_USAGE_LIMIT = 15;
	public static final String PHONE_NUMBER = "PHONE_NUMBER";
	public static final int OTP_Length = 6;
	public static final long OTP_LIFESPAN = 300000;
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
	
	
	//---------------- SMS CONSTANTS --------------------------
	
	public final static int SARV_TEMPLATE_NUMBER_OTP = 1;
	public final static int SARV_TEMPLATE_NUMBER_ACCOUNT_BALANCE = 2;
	public final static int SARV_TEMPLATE_NUMBER_CUST_TRANS_SUCC = 3;
	public final static int SARV_TEMPLATE_NUMBER_CUST_TRANS_FAILED = 4;
	public final static int SARV_TEMPLATE_NUMBER_MERCH_TRANS_SUCC = 5;
	public final static int SARV_TEMPLATE_NUMBER_MERCH_TRANS_FAILED = 6;
	
	
	public final static String SARV_BASE_URL = "http://manage.sarvsms.com/api/send_transactional_sms.php?username=u29627&msg_token=FDHkvR&sender_id=CHGPAY";
	public final static String SARV_TEMPLATE_OTP = "Your+OTP+is+XXXX";
	public final static boolean sendOTPMessage = true;
	public final static String SARV_TEMPLATE_ACCOUNT_BALANCE = "Your+Account+Balance+is+XXXX";
	public final static boolean sendAccBalMessage = true;
	public final static String SARV_TEMPLATE_CUST_TRANS_SUCC = "Your+Transaction+of+XXXX+at+XXXX+was+successful.+Your+Account+Balance+now+is+XXXX";
	public final static boolean sendTransConfirmationtoCustomer = true;
	public final static String SARV_TEMPLATE_CUST_TRANS_FAILED = "Your+transaction+of+XXXX+at+XXXX+was+unsuccessful.+XXXX";
	public final static String SARV_TEMPLATE_MERCH_TRANS_SUCC = "Transaction+of+XXXX,+successful.+Account+Balance+now+is+XXXX";
	public final static boolean sendTransConfirmationtoMerchant = true;
	public final static String SARV_TEMPLATE_MERCH_TRANS_FAILED = "Transaction+of+amount+XXXX+from+XXXX+failed" ;
	
}
