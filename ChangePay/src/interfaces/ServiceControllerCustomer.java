package interfaces;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.css.CSSUnknownRule;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;

import dataAccess.CustomerDAO;
import dataAccess.MerchantDAO;
import domain.Customer;
import domain.Merchant;
import domain.OTP;
import domain.SendSMS;
import exceptions.AccessOverrideException;
import exceptions.InsufficientFundsException;


@Controller
@SuppressWarnings("unchecked")
@RequestMapping(path="/Customer/*")
public class ServiceControllerCustomer {

	public ServiceControllerCustomer() {
		super();
		System.out.println("Setting MongoDB Driver Logging to SEVERE");
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(java.util.logging.Level.SEVERE);

	}
	@RequestMapping(path="/RegisterNewCustomer",method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody String RegisterNewCustomer(HttpServletRequest Request, HttpServletResponse response, @RequestBody Object abc){
		JSONObject bufferJSONObject;
		try{
			String phoneNumber = (String)((LinkedHashMap<Object,Object>)abc).get("phoneNumber");
			//			String jsonString = (String) abc;
			//			JSONParser parser = new JSONParser();
			//			JSONObject newJObject = (JSONObject) parser.parse(jsonString);
			//			String phoneNumber = (String) newJObject.get("phoneNumber");
			new CustomerDAO().createCustomer(phoneNumber,null);
			ObjectMapper mapper = new ObjectMapper();
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "Successful.");
			bufferJSONObject.put("statusCode", 200);
			bufferJSONObject.put("customer", (new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson());
			return bufferJSONObject.toJSONString();
		}
		catch(ParseException | NumberFormatException e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "Invalid Request Data " + e.getMessage());
			bufferJSONObject.put("statusCode", 450);
			//			logger.error(e);
			return bufferJSONObject.toJSONString();
		}
		catch(MongoException e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "DataBase is down.");
			bufferJSONObject.put("statusCode", 550);
			//			logger.error(e);
			return bufferJSONObject.toJSONString();
		}
		catch(IOException e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "Corrupted Data");
			bufferJSONObject.put("statusCode", 551);
			//			logger.error(e);
			return bufferJSONObject.toJSONString();
		}
		catch(Exception e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "App Server has an Internal error.");
			bufferJSONObject.put("statusCode", 500);
			//			logger.error(e);
			return bufferJSONObject.toJSONString();
		}
	}

	@RequestMapping(path="/getOTP",method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody String getOTP(HttpServletRequest Request, HttpServletResponse response, @RequestBody HashMap<Object, Object> newJObject){
		JSONObject bufferJSONObject;
		try{
			String phoneNumber = (String) newJObject.get("phoneNumber");
			if(newJObject.containsKey("amount")){
				float amount = Float.parseFloat(""+newJObject.get("amount"))	;
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				Customer customer = mapper.readValue((new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(), Customer.class);
				try{
				String otp = customer.openTransaction(amount);
				new CustomerDAO().updateObjectWithKey("customerID", customer.getCustomerID(), customer);
				bufferJSONObject = new JSONObject();
				bufferJSONObject.put("status", "Successful.");
				bufferJSONObject.put("statusCode", 200);
				bufferJSONObject.put("otp", otp);
				return bufferJSONObject.toJSONString();
				}
				catch(InsufficientFundsException e) {
					bufferJSONObject = new JSONObject();
					bufferJSONObject.put("status", "Insufficient Funds.");
					bufferJSONObject.put("statusCode", 403);
					return bufferJSONObject.toJSONString();
				}

			}
			else{
				//The OTP is for collecting money or basically for auth purposes.
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				Customer customer = mapper.readValue((new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(), Customer.class);
				String otp = customer.startAuthTransaction();
				bufferJSONObject = new JSONObject();
				bufferJSONObject.put("status", "Successful.");
				bufferJSONObject.put("statusCode", 200);
				bufferJSONObject.put("otp", otp);
				return bufferJSONObject.toJSONString();
			}
		}
			catch(ParseException | NumberFormatException e){
				bufferJSONObject = new JSONObject();
				bufferJSONObject.put("status", "Invalid Request Data " + e.getMessage());
				bufferJSONObject.put("statusCode", 450);
				//			logger.error(e);
				return bufferJSONObject.toJSONString();
			}
			catch(MongoException e){
				bufferJSONObject = new JSONObject();
				bufferJSONObject.put("status", "DataBase is down.");
				bufferJSONObject.put("statusCode", 550);
				//			logger.error(e);
				return bufferJSONObject.toJSONString();
			}
			catch(IOException e){
				bufferJSONObject = new JSONObject();
				bufferJSONObject.put("status", "Corrupted Data");
				bufferJSONObject.put("statusCode", 551);
				//			logger.error(e);
				return bufferJSONObject.toJSONString();
			}
			catch(Exception e){
				bufferJSONObject = new JSONObject();
				bufferJSONObject.put("status", "App Server has an Internal error.");
				bufferJSONObject.put("statusCode", 500);
				//			logger.error(e);
				return bufferJSONObject.toJSONString();
			}
		
	}

	@RequestMapping(path="/Login",method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONObject Login(HttpServletRequest Request, HttpServletResponse response, @RequestBody HashMap<Object, Object> newJObject){
		JSONObject bufferJSONObject;
		try{
			bufferJSONObject = new JSONObject();
			//			JSONParser parser = new JSONParser();
			//			JSONObject newJObject = (JSONObject) parser.parse(jsonString);
			String phoneNumber = (String) newJObject.get("phoneNumber");
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			if(newJObject.keySet().contains("otp")){
				String otp = (String) newJObject.get("otp");
				Customer customer = mapper.readValue((new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(),Customer.class);
				if(customer.login(otp)){
					bufferJSONObject.put("customer", mapper.writeValueAsString(customer));
					new CustomerDAO().updateObjectWithKey("customerID", customer.getCustomerID(), customer);
					bufferJSONObject.put("status", "Successful.");
					bufferJSONObject.put("statusCode", 200);
				}
				else{
					bufferJSONObject.put("status", "Login Failed. Please generate an OTP again.");
					bufferJSONObject.put("statusCode", 201);
				}
			}
			else {
				Customer customer = mapper.readValue((new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(),Customer.class);
				String otp = customer.login();
				//				bufferJSONObject.put("otp", otp);
				HashMap<String, String > smsParameters = new HashMap<>();
				smsParameters.put("phoneNumber", customer.getPhoneNumber());
				smsParameters.put("otp", otp);
				SendSMS.sendSarvMessage(smsParameters, 1);
				new CustomerDAO().updateObjectWithKey("customerID", customer.getCustomerID(), customer);
				bufferJSONObject.put("status", "Successful.");
				bufferJSONObject.put("statusCode", 200);
			}
			return bufferJSONObject;
		}
		catch(AccessOverrideException e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "User Already Logged In");
			bufferJSONObject.put("statusCode", 403);
			return bufferJSONObject;
		}
		catch(ParseException | NumberFormatException e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "Invalid Request Data " + e.getMessage());
			bufferJSONObject.put("statusCode", 450);
			//			logger.error(e);
			return bufferJSONObject;
		}
		catch(MongoException e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "DataBase is down.");
			bufferJSONObject.put("statusCode", 550);
			//			logger.error(e);
			return bufferJSONObject;
		}
		catch(IOException e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "Corrupted Data");
			bufferJSONObject.put("statusCode", 551);
			//			logger.error(e);
			return bufferJSONObject;
		}
		catch(Exception e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "App Server has an Internal error.");
			bufferJSONObject.put("statusCode", 500);
			//			logger.error(e);
			return bufferJSONObject;
		}
	}

	@RequestMapping(path="/getWallet",method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody String getWallet(HttpServletRequest Request, HttpServletResponse response, @RequestBody HashMap<Object, Object> newJObject){
		JSONObject bufferJSONObject;
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			bufferJSONObject = new JSONObject();
			//			JSONParser parser = new JSONParser();
			//			JSONObject newJObject = (JSONObject) parser.parse(jsonString);
			String phoneNumber = (String) newJObject.get("phoneNumber");
			Customer customer = mapper.readValue((new CustomerDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(),Customer.class);
			bufferJSONObject.put("wallet", mapper.writeValueAsString(customer.getWallet()));
			bufferJSONObject.put("status", "Successful.");
			bufferJSONObject.put("statusCode", 200);
			return bufferJSONObject.toJSONString();
		}
		catch(ParseException | NumberFormatException e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "Invalid Request Data " + e.getMessage());
			bufferJSONObject.put("statusCode", 450);
			//			logger.error(e);
			return bufferJSONObject.toJSONString();
		}
		catch(MongoException e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "DataBase is down.");
			bufferJSONObject.put("statusCode", 550);
			//			logger.error(e);
			return bufferJSONObject.toJSONString();
		}
		catch(IOException e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "Corrupted Data");
			bufferJSONObject.put("statusCode", 551);
			//			logger.error(e);
			return bufferJSONObject.toJSONString();
		}
		catch(Exception e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "App Server has an Internal error.");
			bufferJSONObject.put("statusCode", 500);
			//			logger.error(e);
			return bufferJSONObject.toJSONString();
		}
	}

}
