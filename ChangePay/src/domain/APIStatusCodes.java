package domain;

public class APIStatusCodes {
	
	public static final String STATUS_CODE_KEY = "statusCode";
	public static final String STATUS_DESC_KEY = "status";

	public static final int CODE_SUCCESS = 200; 
	public static final int CODE_OTP_EXPIRED = 201;

	public static final int CODE_USER_NOT_FOUND = 401;
	public static final int CODE_INSUFFICIENT_FUNDS = 402;
	public static final int CODE_USER_ALREADY_LOGGED_IN = 403;
	public static final int CODE_CURRENCY_NOT_SUPPORTED = 404;
	public static final int CODE_INVALID_REQUEST_DATA = 450;

	public static final int CODE_INTERNAL_ERROR = 500;
	public static final int CODE_DATABASE_ERROR = 550;
	public static final int CODE_CORRUPTED_DATA_IN_DB = 551;


	public static final String DESC_SUCCESS = "SUCCESS" ;
	public static final String DESC_OTP_EXPIRED = "OTP EXPIRED. TRY AGAIN";

	public static final String DESC_USER_NOT_FOUND = "USER NOT FOUND" ;
	public static final String DESC_INSUFFICIENT_FUNDS = "INSUFFICIENT FUNDS" ;
	public static final String DESC_USER_ALREADY_LOGGED_IN = "USER ALREADY LOGGED IN" ;
	public static final String DESC_CURRENCY_NOT_SUPPORTED = "CURRENCY NOT SUPPORTED" ;
	public static final String DESC_INVALID_REQUEST_DATA = "INVALID REQUEST DATA" ;

	public static final String DESC_INTERNAL_ERROR = "INTERNAL ERROR" ;
	public static final String DESC_DATABASE_ERROR = "DATABASE ERROR" ;
	public static final String DESC_CORRUPTED_DATA_IN_DB = "CORRUPTED DATA IN DB" ;
}
