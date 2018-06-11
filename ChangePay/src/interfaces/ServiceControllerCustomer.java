package interfaces;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.Cookie;
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
import exceptions.InvalidRequestException;

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
			@RequestBody HashMap<Object, Object> newJObject , @CookieValue(value = "sessionID" ,required = true) String sessionID) {
//		String sessionID = 
		JSONResponseBody responseBody = new JSONResponseBody();
		try {
//			String phoneNumber = (String) newJObject.get("phoneNumber");
			Session session = SessionManagerDAO.getSession(sessionID);
			Customer customer = (Customer) Session.getUser(session);
//			Customer customer = mapper.readValue(
//					(new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(), Customer.class);
			customer.logout();
			new CustomerDAO().updateObjectWithKey("customerID", customer.getCustomerID(), customer);
			responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_SUCCESS);
			responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_SUCCESS);
			response.addCookie(CookieManager.getBlankCookie("sessionID"));
//			response.addHeader("Set-Cookie", cookie);
			return responseBody;
		} catch (Exception e) {
			return Constants.handleCustomException(e);
		}
	}
	
	@RequestMapping(path = "/LogoutTest", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONResponseBody logoutTest(HttpServletRequest Request, HttpServletResponse response,
			@RequestBody HashMap<Object, Object> newJObject , @CookieValue(value = "sessionID" ,required = false) String sessionID) {
//		String sessionID = 
		JSONResponseBody responseBody = new JSONResponseBody();
		try {
			String phoneNumber = (String) newJObject.get("phoneNumber");
			Customer customer = mapper.readValue(
					(new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(), Customer.class);
			customer.logout();
			new CustomerDAO().updateObjectWithKey("customerID", customer.getCustomerID(), customer);
			responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_SUCCESS);
			responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_SUCCESS);
			response.addCookie(CookieManager.getBlankCookie("sessionID"));
//			response.addHeader("Set-Cookie", cookie);
			return responseBody;
		} catch (Exception e) {
			return Constants.handleCustomException(e);
		}
	}

	@RequestMapping(path = "/getHotTransactionStatus", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONResponseBody getHotTransactionStatus(HttpServletRequest Request,
			HttpServletResponse response, @RequestBody HashMap<Object, Object> newJObject) {
		JSONResponseBody responseBody = new JSONResponseBody();
		try {
			String phoneNumber;
			Customer customer = null;
			if (newJObject.keySet().contains("sessionKey")) {
				String sessionKey = (String) newJObject.get("sessionKey");
				Session session = SessionManagerDAO.getSession(sessionKey);
				customer = (Customer) Session.getUser(session);
				phoneNumber = customer.getPhoneNumber();
			} else if (newJObject.keySet().contains("phoneNumber")) {
				phoneNumber = (String) newJObject.get("phoneNumber");
				customer = mapper.readValue(
						(new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(),
						Customer.class);
			} else {
				throw new InvalidRequestException();
			}

			// ;
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
//			Cookie cookie = new Cookie("sessionID", "123");
////			cookie.setPath("/");
////			cookie.setMaxAge(1000);
////			cookie.setSecure(false);
////			cookie.setDomain("192.168.0.126");
//			response.addCookie(cookie);
			return responseBody;
		} catch (Exception e) {
			return Constants.handleCustomException(e);
		}
	}

	@RequestMapping(path = "/getOTP", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONResponseBody getOTP(HttpServletRequest Request, HttpServletResponse response,
			@RequestBody HashMap<Object, Object> newJObject) {
		JSONResponseBody responseBody;
		try {
			String phoneNumber = (String) newJObject.get("phoneNumber");
			if (newJObject.containsKey("amount")) {
				float amount = Float.parseFloat("" + newJObject.get("amount"));
				Customer customer = mapper.readValue(
						(new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(),
						Customer.class);
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
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				Customer customer = mapper.readValue(
						(new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(),
						Customer.class);
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
			@RequestBody HashMap<Object, Object> requestData) {
		JSONResponseBody responseBody;
		try {
			responseBody = new JSONResponseBody();
			String phoneNumber = (String) requestData.get("phoneNumber");
			if (requestData.keySet().contains("otp")) {
				String otp = (String) requestData.get("otp");
				Customer customer = mapper.readValue(
						(new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(),
						Customer.class);
				if (customer.login(otp)) {
					responseBody.put("customer", mapper.writeValueAsString(customer));
					new CustomerDAO().updateObjectWithKey("customerID", customer.getCustomerID(), customer);
					responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_SUCCESS);
					responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_SUCCESS);
					if (Request.getSession(false) != null){
						
					}
					Session mySession = new Session(customer.getId(),customer.getPersonType(),null);
//					Session mySession = customer.getloginSessions().get(0) ;//new Session(customer.getId(),customer.getPersonType(),null);
					new SessionManagerDAO().createSession(mySession);
					Cookie cookie = new Cookie("sessionID", mySession.getId());
					response.addCookie(cookie);
					
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
			@RequestBody HashMap<Object, Object> newJObject) {
		JSONResponseBody responseBody;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			responseBody = new JSONResponseBody();
			// JSONParser parser = new JSONParser();
			// JSONResponseBody newJObject = (JSONResponseBody)
			// parser.parse(jsonString);
			String phoneNumber = (String) newJObject.get("phoneNumber");
			Customer customer = mapper.readValue(
					(new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(), Customer.class);
			responseBody.put("wallet", mapper.writeValueAsString(customer.getWallet()));
			responseBody.put(APIStatusCodes.STATUS_DESC_KEY, APIStatusCodes.DESC_SUCCESS);
			responseBody.put(APIStatusCodes.STATUS_CODE_KEY, APIStatusCodes.CODE_SUCCESS);
			return responseBody;
		} catch (Exception e) {
			return Constants.handleCustomException(e);
		}
	}

}
