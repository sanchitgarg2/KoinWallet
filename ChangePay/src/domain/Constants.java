package domain;

import java.io.IOException;

import javax.activity.InvalidActivityException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.mongodb.MongoException;

import exceptions.AccessOverrideException;
import exceptions.ExpiredOrMissingSessionException;
import exceptions.InsufficientFundsException;
import exceptions.InvalidRequestException;

public class Constants {
	public static final String STATUS_LOGGED_IN = "LOGGED_IN";
	public static final String STATUS_LOGGED_OUT = "LOGGED_OUT";
	public static final long SESSION_KEY_EXPIRY = 24 * 60 * 60 * 1000;
	public static final int SESSION_KEY_USAGE_LIMIT = 15;
	public static final String PHONE_NUMBER = "PHONE_NUMBER";
	public static final int OTP_Length = 6;
	public static final long OTP_LIFESPAN = 300000;
	public static final String TRANSACTION_STATUS_INITIATED = "INITIATED";
	public static final String TRANSACTION_STATUS_COMPLETED = "COMPLETED";
	public static final String TRANSACTION_STATUS_FAILED = "FAILED";
	public static final String TRNASACTION_TYPE_AUTH = "AUTH";
	public static final String TRNASACTION_TYPE_CASH = "CASH";
	public static final String TRNASACTION_TYPE_PARTIAL = "PARTIAL";
	public static final String TRNASACTION_TYPE_RECHARGE = "RECHARGE";

	public static final String COLLECTIONS_CUSTOMER = "CUSTOMERS";
	public static final String COLLECTIONS_SESSIONS = "SESSIONS";
	public static final String COLLECTIONS_MERCHANTS = "MERCHANTS";
	public static final String COLLECTIONS_TRANSACTIONS = "TRANSACTIONS";
	public static final String DATABASE_CHANGEPAY = "CHANGEPAY";

	public static final String PERSON_TYPE_CUSTOMER = "CUSTOMER";
	public static final String PERSON_TYPE_MERCHANT = "MERCHANT";

	// ---------------- SMS CONSTANTS --------------------------

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
	public final static String SARV_TEMPLATE_MERCH_TRANS_FAILED = "Transaction+of+amount+XXXX+from+XXXX+failed";
	public static final String CURRENCY_CODE_COMMON_CASH = "CC";
	public static final boolean IS_HTTPS_ENABLED = false;
	public static final String DOMAIN_IP_ADDRESS = "35.224.245.253";
	public static final String SESSION_COOKIE_KEY = "sessionID";
	public static final int COOKIE_AGE = (int)Constants.SESSION_KEY_EXPIRY / 1000 ;

	@SuppressWarnings("unchecked")
	public static JSONResponseBody handleCustomException(Exception e1) {

		JSONResponseBody responseBody;
		responseBody = new JSONResponseBody();
		try {
			throw e1;
		} catch (ExpiredOrMissingSessionException e) {
			responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_INVALID_SESSION_DATA);
			responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_INVALID_REQUEST_DATA);
		}catch (InvalidRequestException e) {
			responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_INVALID_REQUEST_DATA);
			responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_INVALID_REQUEST_DATA);
		} catch (InsufficientFundsException e) {
			responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_INSUFFICIENT_FUNDS);
			responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_INSUFFICIENT_FUNDS);
		} catch (AccessOverrideException e) {
			responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_USER_ALREADY_LOGGED_IN);
			responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_USER_ALREADY_LOGGED_IN);
		} catch (ParseException | NumberFormatException e) {
			responseBody.put(APIStatusCodes.STATUS_DESC_KEY,
					APIStatusCodes.DESC_INVALID_REQUEST_DATA + e.getMessage());
			responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_INVALID_REQUEST_DATA);
			// logger.error(e);
		} catch (MongoException e) {
			responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_DATABASE_ERROR);
			responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_DATABASE_ERROR);
			// logger.error(e);
		} catch (IOException e) {
			responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_CORRUPTED_DATA_IN_DB);
			responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_CORRUPTED_DATA_IN_DB);
			// logger.error(e);
		} catch (Exception e) {
			responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_INTERNAL_ERROR);
			responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_INTERNAL_ERROR);
			// logger.error(e);
		}
		return responseBody;
	}
}
