package interfaces;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dataAccess.CustomerDAO;
import dataAccess.SessionManagerDAO;
import domain.APIStatusCodes;
import domain.Constants;
import domain.CookieManager;
import domain.Customer;
import domain.SendSMS;
import domain.Session;
import domain.Transaction;
import domain.JSONResponseBody;
import exceptions.ExpiredOrMissingSessionException;

@Controller
@SuppressWarnings("unchecked")
@RequestMapping(path = "/Customer/*")
public class ServiceControllerCustomer {
	public static final ObjectMapper mapper = new ObjectMapper();

	public ServiceControllerCustomer() {
		super();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		System.out.println("Setting MongoDB Driver Logging to SEVERE");
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(java.util.logging.Level.SEVERE);

	}

	@RequestMapping(path = "/RegisterNewCustomer", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONResponseBody RegisterNewCustomer(HttpServletRequest Request, HttpServletResponse response,
			@RequestBody Object abc) {
		JSONResponseBody responseBody;
		try {
			// TODO:Generate an OTP and verify the phone Number used. Also throw
			// an error if the customer already exists.
			String phoneNumber = (String) ((LinkedHashMap<Object, Object>) abc).get("phoneNumber");
			new CustomerDAO().createCustomer(phoneNumber, null);
			responseBody = new JSONResponseBody();
			responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_SUCCESS);
			responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_SUCCESS);
			responseBody.put("customer",
					(new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson());
			return responseBody;
		} catch (Exception e) {
			return Constants.handleCustomException(e);
		}
	}

	@RequestMapping(path = "/Logout", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONResponseBody logout(HttpServletRequest Request, HttpServletResponse response,
			@RequestBody HashMap<Object, Object> newJObject,
			@CookieValue(value = Constants.SESSION_COOKIE_KEY, required = false) String sessionID) {

		// String sessionID =
		JSONResponseBody responseBody = new JSONResponseBody();
		try {
			if(sessionID==null){
				throw new ExpiredOrMissingSessionException();
				}
			// String phoneNumber = (String) newJObject.get("phoneNumber");
			Session session = SessionManagerDAO.getSession(sessionID);
			Customer customer = (Customer) Session.getUser(session);
			customer.logout();
			session.endSession();
			new CustomerDAO().updateObjectWithKey("customerID", customer.getCustomerID(), customer);
			responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_SUCCESS);
			responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_SUCCESS);
			response.addCookie(CookieManager.getBlankCookie(Constants.SESSION_COOKIE_KEY));
			return responseBody;
		} catch (Exception e) {
			return Constants.handleCustomException(e);
		}
	}

	@RequestMapping(path = "/LogoutTest", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONResponseBody logoutTest(HttpServletRequest Request, HttpServletResponse response,
			@RequestBody HashMap<Object, Object> newJObject,
			@CookieValue(value = Constants.SESSION_COOKIE_KEY, required = false) String sessionID) {
		// String sessionID =
		JSONResponseBody responseBody = new JSONResponseBody();
		try {
			String phoneNumber = (String) newJObject.get("phoneNumber");
			Customer customer = mapper.readValue(
					(new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(), Customer.class);
			customer.logout();
			new CustomerDAO().updateObjectWithKey("customerID", customer.getCustomerID(), customer);
			responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_SUCCESS);
			responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_SUCCESS);
			response.addCookie(CookieManager.getBlankCookie(Constants.SESSION_COOKIE_KEY));
			// response.addHeader("Set-Cookie", cookie);
			return responseBody;
		} catch (Exception e) {
			return Constants.handleCustomException(e);
		}
	}

	@RequestMapping(path = "/getHotTransactionStatus", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONResponseBody getHotTransactionStatus(HttpServletRequest Request,
			HttpServletResponse response, @RequestBody HashMap<Object, Object> newJObject,
			@CookieValue(value = Constants.SESSION_COOKIE_KEY, required = false) String sessionID) {

		JSONResponseBody responseBody = new JSONResponseBody();
		try {
			if(sessionID==null){
				throw new ExpiredOrMissingSessionException();
				}
			Customer customer = null;
			Session session = SessionManagerDAO.getSession(sessionID);
			customer = (Customer) Session.getUser(session);
			customer = (Customer) Session.getUser(session);
			String otp = (String) newJObject.get("otp");
			Transaction transaction = customer.getHotTransactionStatus(otp);
			if (transaction != null) {
				responseBody.put("transaction", mapper.writeValueAsString(transaction));
				responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_SUCCESS);
				responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_SUCCESS);
			} else {
				responseBody.put(APIStatusCodes.STATUS_DESC_KEY,
						APIStatusCodes.DESC_INTERNAL_ERROR + "Transaction is null");
				responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_INTERNAL_ERROR);
			}
			return responseBody;
		} catch (Exception e) {
			return Constants.handleCustomException(e);
		}
	}

	@RequestMapping(path = "/getOTP", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONResponseBody getOTP(HttpServletRequest Request, HttpServletResponse response,
			@RequestBody HashMap<Object, Object> newJObject,
			@CookieValue(value = Constants.SESSION_COOKIE_KEY, required = false) String sessionID) {

		JSONResponseBody responseBody;
		try {
			if(sessionID==null){
				throw new ExpiredOrMissingSessionException();
				}
			// String phoneNumber = (String) newJObject.get("phoneNumber");
			Session session = SessionManagerDAO.getSession(sessionID);
			Customer customer = (Customer) Session.getUser(session);
			if (newJObject.containsKey("amount")) {
				float amount = Float.parseFloat("" + newJObject.get("amount"));
				String otp = customer.openTransaction(amount);
				new CustomerDAO().updateObjectWithKey("customerID", customer.getCustomerID(), customer);
				responseBody = new JSONResponseBody();
				responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_SUCCESS);
				responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_SUCCESS);
				responseBody.put("otp", otp);
				return responseBody;
			} else {
				// The OTP is for collecting money or basically for auth
				// purposes.
				String otp = customer.startAuthTransaction();
				responseBody = new JSONResponseBody();
				responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_SUCCESS);
				responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_SUCCESS);
				responseBody.put("otp", otp);
				return responseBody;
			}
		} catch (Exception e) {
			return Constants.handleCustomException(e);
		}

	}

	@RequestMapping(path = "/Login", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONResponseBody Login(HttpServletRequest Request, HttpServletResponse response,
			@RequestBody HashMap<Object, Object> requestData ,
			@CookieValue(value = Constants.SESSION_COOKIE_KEY, required = false) String sessionID) {
		JSONResponseBody responseBody;
		try {
			responseBody = new JSONResponseBody();
			String phoneNumber = (String) requestData.get("phoneNumber");
			if(sessionID != null){
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				responseBody = new JSONResponseBody();
				Session session = SessionManagerDAO.getSession(sessionID);
				Customer customer = (Customer) Session.getUser(session);
				responseBody.put("customer", mapper.writeValueAsString(customer));
				responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_SUCCESS);
				responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_SUCCESS);
			}
			if (requestData.keySet().contains("otp")) {
				String otp = (String) requestData.get("otp");
				Customer customer = mapper.readValue(
						(new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(),
						Customer.class);
				if (customer.login(otp)) {
					new CustomerDAO().updateObjectWithKey("customerID", customer.getCustomerID(), customer);
					responseBody.put("customer", mapper.writeValueAsString(customer));
					response.addCookie(CookieManager.getCookie(Constants.SESSION_COOKIE_KEY,
							customer.getLoginSessions().get(0).getId()));
					responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_SUCCESS);
					responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_SUCCESS);
				} else {
					responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_OTP_EXPIRED);
					responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_OTP_EXPIRED);
				}
			} else {
				Customer customer = mapper.readValue(
						(new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(),
						Customer.class);
				String otp = customer.login();
				// responseBody.put("otp", otp);
				HashMap<String, String> smsParameters = new HashMap<>();
				smsParameters.put("phoneNumber", customer.getPhoneNumber());
				smsParameters.put("otp", otp);
				SendSMS.sendSarvMessage(smsParameters, 1);
				new CustomerDAO().updateObjectWithKey("customerID", customer.getCustomerID(), customer);
				responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_SUCCESS);
				responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_SUCCESS);
			}
			return responseBody;
		} catch (Exception e) {
			return Constants.handleCustomException(e);
		}
	}

	@RequestMapping(path = "/getWallet", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONResponseBody getWallet(HttpServletRequest Request, HttpServletResponse response,
			@RequestBody HashMap<Object, Object> newJObject , @CookieValue(value = Constants.SESSION_COOKIE_KEY, required = false) String sessionID) {
		JSONResponseBody responseBody;
		try {
			if(sessionID==null){
				throw new ExpiredOrMissingSessionException();
				}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			responseBody = new JSONResponseBody();
			Session session = SessionManagerDAO.getSession(sessionID);
			Customer customer = (Customer) Session.getUser(session);
			responseBody.put("wallet", mapper.writeValueAsString(customer.getWallet()));
			responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_SUCCESS);
			responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_SUCCESS);
			return responseBody;
		} catch (Exception e) {
			return Constants.handleCustomException(e);
		}
	}

}
