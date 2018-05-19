package interfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;

import dataAccess.CustomerDAO;
import dataAccess.MerchantDAO;
import dataAccess.TransactionDAO;
import domain.Constants;
import domain.Customer;
import domain.Merchant;
import domain.PaymentObject;
import domain.Transaction;
import exceptions.MessageNotSentException;


@Controller
@SuppressWarnings("unchecked")
@RequestMapping(path="/Merchant/*")
public class ServiceControllerMerchant {

	public ServiceControllerMerchant() {
		super();
		System.out.println("Setting MongoDB Driver Logging to SEVERE");
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(java.util.logging.Level.SEVERE);

	}

	@RequestMapping(path="/RegisterNewMerchant",method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONObject RegisterNewMerchant(HttpServletRequest Request, HttpServletResponse response, @RequestBody HashMap<Object, Object> newJObject){
		JSONObject bufferJSONObject;
		try{
			//			JSONParser parser = new JSONParser();
			//			JSONObject newJObject = (JSONObject) parser.parse(jsonString);
			String phoneNumber = (String) newJObject.get("phoneNumber");
			String govtAuthNumber = (String) newJObject.get("govtAuthNumber");
			String govtAuthType = (String) newJObject.get("govtAuthType");
			String name = (String) newJObject.get("name");
			String address = (String) newJObject.get("address");	
			new MerchantDAO().createMerchant(phoneNumber, govtAuthNumber, govtAuthType, name, address);
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "Successful.");
			bufferJSONObject.put("statusCode", 200);
			bufferJSONObject.put("merchant", (new MerchantDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson());
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

	@RequestMapping(path="/Login",method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONObject Login(HttpServletRequest Request, HttpServletResponse response, @RequestBody HashMap<Object, Object> newJObject){
		JSONObject bufferJSONObject;
		try{
			bufferJSONObject = new JSONObject();
			String phoneNumber = (String) newJObject.get("phoneNumber");
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			if(newJObject.keySet().contains("otp")){
				String otp = (String) newJObject.get("otp");
				Merchant merchant = mapper.readValue((new MerchantDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(),Merchant.class);
				if(merchant.login(otp)){
					new MerchantDAO().updateObjectWithKey("merchantID", merchant.getMerchantID(),merchant);
					bufferJSONObject.put("merchant", mapper.writeValueAsString(merchant));
					bufferJSONObject.put("status", "Successful.");
					bufferJSONObject.put("statusCode", 200);
				}
				else{
					bufferJSONObject.put("status", "Login Failed. Please generate an OTP again.");
					bufferJSONObject.put("statusCode", 400);
				}
			}
			else {
				Merchant merchant = mapper.readValue((new MerchantDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(),Merchant.class);
				try{
					String otp = merchant.login();
					new MerchantDAO().updateObjectWithKey("merchantID", merchant.getMerchantID() , merchant);
					bufferJSONObject.put("status", "Successful.");
					bufferJSONObject.put("statusCode", 200);
				}
				catch (MessageNotSentException e) {
					bufferJSONObject.put("status", "Sending SMS failed. Retry after some time.");
					bufferJSONObject.put("statusCode", 401);
				}
			}
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
	
	@RequestMapping(path="/Logout",method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONObject logout(HttpServletRequest Request, HttpServletResponse response, @RequestBody HashMap<Object, Object> newJObject){
		JSONObject bufferJSONObject = new JSONObject();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		try{
			String phoneNumber = (String) newJObject.get("phoneNumber");
			Merchant merchant = mapper.readValue((new MerchantDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(), Merchant.class);
			merchant.logout();
			new MerchantDAO().updateObjectWithKey("merchantID", merchant.getMerchantID(), merchant);
			bufferJSONObject.put("status", "Successful.");
			bufferJSONObject.put("statusCode", 200);
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
	
	@RequestMapping(path="/Recharge",method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONObject Recharge(HttpServletRequest Request, HttpServletResponse response, @RequestBody HashMap<Object, Object> requestData){
		JSONObject responseData;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try{
			responseData = new JSONObject();
			String phoneNumber = (String) requestData.get("phoneNumber");
			String otp = (String) requestData.get("otp");
			float amount = Float.parseFloat("" + requestData.get("amount"));
			
			Merchant merchant = mapper.readValue((new MerchantDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(),Merchant.class);
			Transaction t = new Transaction();
			t.setMerchantID(merchant.getMerchantID());
			t.setRequestedAmount(amount);
			t.setStatus(Constants.TRANSACTION_STATUS_INITIATED);
			t.setOTP(null);
			t.setTransactionType(Constants.TRNASACTION_TYPE_RECHARGE);
			ArrayList<PaymentObject> payment = new ArrayList<>();
			if(merchant.getUpperLimit() < merchant.getWallet().getNetWorth() - amount)
			{
				//TODO:fix the total amount calculation later.
				PaymentObject p = new PaymentObject();
				p.setCurrencyCode(Constants.CURRENCY_CODE_COMMON_CASH);
//				p.setAmount(amount);
				p.setGenerationTimeStamp(System.currentTimeMillis()+"");
				p.setSource(merchant.getMerchantID());
				p.setAmount(amount*-1);
				merchant.getWallet().processPayment(p, t.getTransactionRefNo());
				p.setAmount(p.getAmount()*-1);
				//TODO:Add code here for the CashBack generation.
//				p.setAmount(amount);
				payment.add(p);
				Transaction authTransaction = mapper.readValue((new TransactionDAO()).getObjectByKeyValuePair("otp.OTP", otp).toJson(),Transaction.class);
				Customer customer = mapper.readValue(new CustomerDAO().getObjectByKeyValuePair("customerID", authTransaction.getCustomerID()).toJson() , Customer.class );
				customer.acceptPayment(payment , t.getTransactionRefNo() , otp);
				t.setPayment(payment);
				t.setStatus(Constants.TRANSACTION_STATUS_COMPLETED);
				t.setAuthTransaction(authTransaction);
				(new MerchantDAO()).updateObjectWithKey("merchantID", merchant.getMerchantID(), merchant);
				(new CustomerDAO()).updateObjectWithKey("customerID", customer.getCustomerID(), customer);
				(new TransactionDAO()).updateObjectWithKey("transactionRefNo", t.getTransactionRefNo(), t);
				t.setCustomerID(null);
				responseData.put("transaction", mapper.writeValueAsString(t));
				responseData.put("status", "Successful");
				responseData.put("statusCode", 200);
			}
			else{
				responseData.put("status", "Insufficient Balance.");
				responseData.put("statusCode", 402);
			}
			
			return responseData;
		}
		catch(ParseException | NumberFormatException e){
			responseData = new JSONObject();
			responseData.put("status", "Invalid Request Data " + e.getMessage());
			responseData.put("statusCode", 450);
			//			logger.error(e);
			return responseData;
		}
		catch(MongoException e){
			responseData = new JSONObject();
			responseData.put("status", "DataBase is down.");
			responseData.put("statusCode", 550);
			//			logger.error(e);
			return responseData;
		}
		catch(IOException e){
			responseData = new JSONObject();
			responseData.put("status", "Corrupted Data");
			responseData.put("statusCode", 551);
			//			logger.error(e);
			return responseData;
		}
		catch(Exception e){
			responseData = new JSONObject();
			responseData.put("status", "App Server has an Internal error.");
			responseData.put("statusCode", 500);
			//			logger.error(e);
			return responseData;
		}
	}

	@RequestMapping(path="/claimOTP",method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody JSONObject claimOTP(HttpServletRequest Request, HttpServletResponse response, @RequestBody HashMap<Object, Object> newJObject){
		JSONObject bufferJSONObject;
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			bufferJSONObject = new JSONObject();
			String phoneNumber = (String) newJObject.get("phoneNumber");
			String otp = (String) newJObject.get("otp");
			float amount = Float.parseFloat(""+newJObject.get("amount"));
			Merchant merchant = mapper.readValue((new MerchantDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(),Merchant.class);
			merchant.claimOTP(otp, amount);
			bufferJSONObject.put("wallet", mapper.writeValueAsString(merchant.getWallet()));
			bufferJSONObject.put("status", "Successful.");
			bufferJSONObject.put("statusCode", 200);
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
	public @ResponseBody JSONObject getWallet(HttpServletRequest Request, HttpServletResponse response, @RequestBody HashMap<Object, Object> newJObject){
		JSONObject bufferJSONObject;
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			bufferJSONObject = new JSONObject();
			String phoneNumber = (String) newJObject.get("phoneNumber");
			Merchant merchant = mapper.readValue((new MerchantDAO().getObjectByKeyValuePair("phoneNumber", phoneNumber)).toJson(),Merchant.class);
			bufferJSONObject.put("wallet", mapper.writeValueAsString(merchant.getWallet()));
			bufferJSONObject.put("status", "Successful.");
			bufferJSONObject.put("statusCode", 200);
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
	//Recharge a customer
	//Transfer to a customer
	//Pay a merchant
	//Redeem Money for Merchants
	//Generate money


}
