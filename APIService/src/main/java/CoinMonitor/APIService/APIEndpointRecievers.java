package CoinMonitor.APIService;

import static com.mongodb.client.model.Filters.eq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import CoinMonitor.APIService.Currency.CurrencySnapShot;
import CoinMonitor.APIService.Exceptions.UserNotFoundException;

@Controller
public class APIEndpointRecievers {
	public APIEndpointRecievers() {
		super();
		System.out.println("Setting MongoDB Driver Logging to SEVERE");
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(java.util.logging.Level.SEVERE);
	}

	public final static Logger logger = LogManager.getLogger(APIEndpointRecievers.class);
	private static MongoClient mongoClient;
	private static MongoDatabase database;

	public static MongoClient getMongoClient() {
		if(mongoClient == null)
			initializeMongoClient();
		return mongoClient;
	}
	public static void setMongoClient(MongoClient mongoClient) {
		APIEndpointRecievers.mongoClient = mongoClient;
	}
	public static MongoDatabase getDatabase() {
		if(database == null)
			initializeMongoClient();
		return APIEndpointRecievers.database;
	}
	public static Logger getLogger() {
		return logger;
	}
	public static void initializeMongoClient()
	{
		logger.log(Level.INFO,"Initializing MongoClient.");
		if(APIEndpointRecievers.mongoClient == null){
			try{
			APIEndpointRecievers.mongoClient = new MongoClient();
			}
			catch(Exception e){
				logger.error("Error in Initialzing Mongo Client");
				throw e;
			}
		}
		APIEndpointRecievers.database = mongoClient.getDatabase(ApplicationConstants.DATABASE);
		logger.info("Mongo client initialized successfully.");

	}

	@RequestMapping(path="/Trade",method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody String Trade(HttpServletRequest Request, HttpServletResponse response, @RequestBody String jsonString){
		JSONObject bufferJSONObject;
		try{
			logger.info("Recieved Trade request");
			JSONParser parser = new JSONParser();
			JSONObject newJObject = (JSONObject) parser.parse(jsonString);
			int userID = Integer.parseInt((String)newJObject.get("userID"));
			User user = getUser(userID);
			String currencyCode = (String) newJObject.get("currencyCode");
			float quantity = Float.parseFloat((String)newJObject.get("quantity"));
			float price = Float.parseFloat((String)newJObject.get("price"));
			logger.info("Request Parameters: UserID : "+userID + " Currency : " + currencyCode +" Quantity: " + quantity);
			Transaction transaction;
			if(quantity > 0)
				transaction = new Transaction(Currency.getCURRENCYSTATE().get("INR"), Currency.getCURRENCYSTATE().get(currencyCode), price, quantity);
			else if(quantity < 0)
				transaction = new Transaction(Currency.getCURRENCYSTATE().get(currencyCode), Currency.getCURRENCYSTATE().get("INR"), 1/price, quantity*price);
			else{
				logger.info("API Request with quantity 0 made.");
				throw new NumberFormatException("Please enter a non zero quantity.");
			}
			user.trade(transaction);
			updateUser(user);
			
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "Successful.");
			bufferJSONObject.put("statusCode", 200);
			return bufferJSONObject.toJSONString();
//			return ""+true;
		}
		catch(ParseException | NumberFormatException e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "Invalid Request Data " + e.getMessage());
			bufferJSONObject.put("statusCode", 450);
			logger.error(e);
			return bufferJSONObject.toJSONString();
		}
		catch(MongoException e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "DataBase is down.");
			bufferJSONObject.put("statusCode", 550);
			logger.error(e);
			return bufferJSONObject.toJSONString();
		}
		catch(IOException e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "Corrupted Data");
			bufferJSONObject.put("statusCode", 551);
			logger.error(e);
			return bufferJSONObject.toJSONString();
		}
		catch(Exception e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "App Server has an Internal error.");
			bufferJSONObject.put("statusCode", 500);
			logger.error(e);
			return bufferJSONObject.toJSONString();
		}
	}

	@RequestMapping(path = "/getWatchList", method = RequestMethod.POST)
	public @ResponseBody String getWatchList(HttpServletRequest Request, HttpServletResponse response,
			@RequestBody String jsonString) {
		JSONObject bufferJSONObject;
		JSONParser parser = new JSONParser();
		try {
			logger.info("Received a getWatchList request");
			bufferJSONObject = (JSONObject) parser.parse(jsonString);
			int userID = Integer.parseInt("" + bufferJSONObject.get("userID"));
			List<Currency> watchList = getUser(userID).getWatchList();
			logger.info("Request Parameters -> UserID :" + userID);
			String s;
			for (Currency c : watchList) {
				try {
					s = (Currency.getCurrency(c.currencyCode)).getValue().toJSONString();
					bufferJSONObject.put(c.currencyCode, s);
				} catch (Exception e) {
					logger.error("Currency " + c + "in WatchList, but does not exist.");
				}

			}
			logger.info("Served Request successfully.");
			bufferJSONObject.put("status", "Successful.");
			bufferJSONObject.put("statusCode", 200);
			return bufferJSONObject.toJSONString();
		} catch (MongoException e) {
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "DataBase is down.");
			bufferJSONObject.put("statusCode", 550);
			logger.error(e);
			return bufferJSONObject.toJSONString();
		} catch (IOException e) {
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "Corrupted Data");
			bufferJSONObject.put("statusCode", 551);
			logger.error(e);
			return bufferJSONObject.toJSONString();
		} catch (Exception e) {
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "App Server is facing an Internal issue");
			bufferJSONObject.put("statusCode", 500);
			logger.error(e);
			return bufferJSONObject.toJSONString();

		}
	}

	@RequestMapping(path="/Register" , method = RequestMethod.POST)
	public @ResponseBody String Register(HttpServletRequest Request, HttpServletResponse response, @RequestBody String jsonString){
		JSONObject bufferJSONObject;
		try{
			logger.info("Received a Register request.");
			JSONParser parser = new JSONParser();
			bufferJSONObject = null;
			bufferJSONObject = (JSONObject) parser.parse(jsonString);
			String email = "" + bufferJSONObject.get("email");
			String phoneNumber = "" + bufferJSONObject.get(ApplicationConstants.PHONE_NUMBER);
			String countryCode = "" + bufferJSONObject.get("countryCode");
			String deviceId = "" + bufferJSONObject.get("deviceID");
			logger.info("Request Parameters : Phone Number : " + phoneNumber +" emailID : "+ email + " countryCode : " + countryCode + " devideID : " + deviceId );
			JSONObject returnObject = new JSONObject();
			User user  = isValidUser(ApplicationConstants.PHONE_NUMBER, phoneNumber);
			logger.info("Parameters verified. Unique Phone Number checked.");
			if(user != null)
			{
				returnObject.put("status", "User with that phone number already exists");
				returnObject.put("statusCode", 400);
				return returnObject.toJSONString();
			}
			User newUser = new User();
			newUser.setCountryCode(countryCode);
			newUser.setEmailID(email);
			newUser.setLastUsedDeviceID(deviceId);
			newUser.setLiquidCashInWallet(0);
			newUser.setPhoneNumber(phoneNumber);
			newUser.setWallet(new CoinWallet());
			newUser.setWatchList(null);

			if(User.getUnverifiedUsers().values().contains(newUser))
			{
				
				OTP otp1=null ;
				for(OTP otp : User.getUnverifiedUsers().keySet())
					if(((User)User.getUnverifiedUsers().get(otp)).getPhoneNumber().equals(newUser.getPhoneNumber()))
					{otp1 = otp;break;}
				sendSMS("Your OTP is "+ otp1.getOtp(), newUser.getPhoneNumber());
				returnObject.put("status", "OTP already generated.");
				returnObject.put("statusCode", 401);
				return returnObject.toJSONString();
			}
			else{
				
			}
			int userID = GenerateUserID(newUser); 
			newUser.setUSERID(userID);
			OTP OTP = GenerateOTP(newUser);
			while(User.getUnverifiedUsers().containsKey(OTP))
				OTP = GenerateOTP(newUser);
			sendSMS("Your OTP is "+OTP.getOtp(), newUser.getPhoneNumber());
			User.getUnverifiedUsers().put(OTP, newUser);
			returnObject.put("status", "Successful");
			returnObject.put("statusCode", 200);
			return returnObject.toJSONString();
		}
		catch(Exception e){
			return null;
		}
	}

	@RequestMapping(path="/Login" , method = RequestMethod.POST)
	public @ResponseBody String Login(HttpServletRequest Request, HttpServletResponse response, @RequestBody String jsonString){
		try{
			JSONParser parser = new JSONParser();
			JSONObject bufferJSONObject = null;
			bufferJSONObject = (JSONObject) parser.parse(jsonString);
			String email = null;
			String phoneNumber = null;
			JSONObject returnObject = new JSONObject();
			try{
			String deviceId = "" + bufferJSONObject.get("deviceID");}
			catch(Exception e){
				returnObject.put(ApplicationConstants.STATUS,e.getMessage() + "Invalid Request Data.");
				returnObject.put(ApplicationConstants.STATUS_CODE, 403);
				return returnObject.toJSONString();
			}
			
			if(bufferJSONObject.keySet().contains(ApplicationConstants.EMAIL) && bufferJSONObject.keySet().contains(ApplicationConstants.PHONE_NUMBER))
			{
				if(bufferJSONObject.containsKey(ApplicationConstants.OTP))
				{
					OTP otp = new OTP();
					otp.setOtp((String) bufferJSONObject.get(ApplicationConstants.OTP));
					if(User.getUnverifiedUsers().containsKey(otp)){
						String Email = (String) bufferJSONObject.get(ApplicationConstants.EMAIL);
						String PhoneNumber = (String) bufferJSONObject.get(ApplicationConstants.PHONE_NUMBER);
						User user = User.getUnverifiedUsers().get(otp);
					if(Email.equals(user.getEmailID()) && PhoneNumber.equals(user.getPhoneNumber()))
						{
							createUser(user);
							returnObject.put("status", "Successful");
							returnObject.put("statusCode", 200);
							returnObject.put("user", getUser(null, null, ""+user.getUSERID()));
							returnObject.put("currencyList", getCurrencyList(null, null));
							return returnObject.toJSONString();
						}	
					}
					else{
						returnObject.put("status", "OTP verification Failed.");
						returnObject.put("statusCode", 401);
						return returnObject.toJSONString();
					}
				}
				else{
					returnObject.put(ApplicationConstants.STATUS, "Invalid Request Data.");
					returnObject.put(ApplicationConstants.STATUS_CODE, 403);
					return returnObject.toJSONString();
				}
			}
			
			if(bufferJSONObject.containsKey(ApplicationConstants.OTP))
			{
				OTP otp = new OTP();
				otp.setOtp((String) bufferJSONObject.get(ApplicationConstants.OTP));
				if(User.getUsersLoggingIn().containsKey(otp)){
					String key;
					String value;
					if(bufferJSONObject.keySet().contains(ApplicationConstants.EMAIL))
					{
						key = ApplicationConstants.EMAIL;
						value = "" + bufferJSONObject.get(ApplicationConstants.EMAIL);
					}
					else
					{
						key = ApplicationConstants.PHONE_NUMBER;
						value = "" + bufferJSONObject.get(ApplicationConstants.PHONE_NUMBER);
					}
					User user = User.getUsersLoggingIn().get(otp);
					if(value.equals(user.getEmailID())|| value.equals(user.getPhoneNumber()))
					{
						
						returnObject.put("status", "Successful");
						returnObject.put("statusCode", 200);
						returnObject.put("user", getUser(null, null, ""+user.getUSERID()));
						returnObject.put("currencyList", getCurrencyList(null, null));
						return returnObject.toJSONString();
					}
					else{
						returnObject.put("status", "OTP verification Failed.");
						returnObject.put("statusCode", 401);
						return returnObject.toJSONString();
					}
				}
				else{
					returnObject.put("status", "OTP verification Failed.");
					returnObject.put("statusCode", 401);
					return returnObject.toJSONString();
				}
				
				
			//end of if bufferJSONObject.containsKey("otp")
			}
			else{
				String key;
				String value;
				if(bufferJSONObject.keySet().contains("email"))
				{
					key = ApplicationConstants.EMAIL;
					value = "" + bufferJSONObject.get("email");
				}
				else
				{
					key = ApplicationConstants.PHONE_NUMBER;
					value = "" + bufferJSONObject.get(ApplicationConstants.PHONE_NUMBER);
				}
				User user = isValidUser(key, value);
				if(user != null)
				{
					try{
						OTP otp = GenerateOTP(user);
						while(User.getUsersLoggingIn().containsKey(otp))
							otp = GenerateOTP(user);
						sendSMS("Your OTP is "+ otp.getOtp(), user.getPhoneNumber());
						User.getUsersLoggingIn().put(otp, user);
						returnObject.put("status", "Successful");
						returnObject.put("statusCode", 200);
						return returnObject.toJSONString();						
					}
					catch(IOException e){
						returnObject.put("status", "Sending SMS failed. Please try after some time.");
						returnObject.put("statusCode", 402);
						return returnObject.toJSONString();
					}					
				}
				else{
					returnObject.put("status", "User does not exist");
					returnObject.put("statusCode", 400);
					return returnObject.toJSONString();
				}
				
			}
			
/*
			if(User.getUnverifiedUsers().values().contains(newUser))
			{
				OTP otp1=null ;
				for(OTP otp : User.getUnverifiedUsers().keySet())
					if(((User)User.getUnverifiedUsers().get(otp)).getPhoneNumber().equals(newUser.getPhoneNumber()))
					{otp1 = otp;break;}
				sendSMS("Your OTP is "+ otp1.getOtp(), newUser.getPhoneNumber());
				returnObject.put("status", "OTP already generated.");
				returnObject.put("statusCode", 401);
				return returnObject.toJSONString();
			}
			int userID = GenerateUserID(newUser); 
			newUser.setUSERID(userID);
			OTP OTP = GenerateOTP(newUser);

			User.getUnverifiedUsers().put(OTP, newUser);
			returnObject.put("status", "Successful");
			returnObject.put("statusCode", 200);
			return returnObject.toJSONString();*/
		}
		catch(Exception e){
			return null;
		}
	}

	
	@RequestMapping(path="/getCurrencyList" , method = RequestMethod.GET)
	public @ResponseBody String getCurrencyList(HttpServletRequest Request, HttpServletResponse response){
		try{
			JSONObject bufferJSONObject = new JSONObject();
			String s;
			for(Currency c:Currency.getCURRENCYSTATE().values())
			{
				s = (Currency.getCURRENCYSTATE().get(c.currencyCode)).getValue().toJSONString();
				bufferJSONObject.put(c.currencyCode, s);
			}
			return bufferJSONObject.toJSONString();
		}
		catch(Exception e){
			logger.error("Currency List null. Error message is " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(path="/updateWatchList" , method = RequestMethod.POST)
	public @ResponseBody String updateWatchList(HttpServletRequest Request, HttpServletResponse response, @RequestBody String jsonString){
		try{
			JSONParser parser = new JSONParser();
			JSONObject bufferJSONObject = null;
			bufferJSONObject = (JSONObject) parser.parse(jsonString);
			int userID = Integer.parseInt("" + bufferJSONObject.get("userID"));
			String currencyCode = bufferJSONObject.get("currencyCode").toString();
			//			ObjectMapper mapper = new ObjectMapper();
			User user = getUser(userID);
			List<Currency> watchList = user.getWatchList();
			if(watchList.contains(Currency.getCURRENCYSTATE().get(currencyCode)))
				watchList.remove(Currency.getCURRENCYSTATE().get(currencyCode));
			else watchList.add(Currency.getCURRENCYSTATE().get(currencyCode));
			user.setWatchList(watchList);
			updateUser(user);
			return ""+true;
		}
		catch(Exception e){
			logger.error("Update WatchList Failed with Error " + e.getMessage() , jsonString , Currency.getCURRENCYSTATE());
			return ""+false;
		}
	}

	@RequestMapping(path="/getUpdate" , method = RequestMethod.GET)
	public @ResponseBody String getCurrencyState() throws Exception{
		JSONObject JsonObject = new JSONObject();
		String s = "";
		for (Currency c : Currency.getCURRENCYSTATE().values()){
			s = c.getValue().toJSONString();
			JsonObject.put(c.currencyCode, s);
		}
		return JsonObject.toJSONString();
	}

	@RequestMapping(path="/getUser",params = {"userID"})
	public @ResponseBody String getUser(HttpServletRequest Request, HttpServletResponse response,  @RequestParam(value = "userID") String userIDString) throws Exception{

		ObjectMapper m = new ObjectMapper();
		int userID = Integer.parseInt(userIDString);
		//		System.out.println("\nClass toString");
		//		System.out.println(sanchit);
		try {
			String s = m.writeValueAsString(getUser(userID));
			return s;
		} catch (JsonProcessingException e) {
			return "INVALID_USER";
		}
	}

	private void createUser(User user) throws Exception {
		MongoCollection<Document> collection = APIEndpointRecievers.getDatabase().getCollection("Users");
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(user);
		collection.insertOne(Document.parse(jsonInString));
	}
	
	private void updateUser(User user) throws Exception {
		try{
			MongoCollection<Document> collection = APIEndpointRecievers.getDatabase().getCollection("Users");
			Document myDoc = collection.find(eq("userid", user.USERID)).first();
			if(myDoc==null) {
				System.out.println("User not registered");
			}
			logger.info("User with ID " + user.USERID +" present");		
			ObjectMapper mapper = new ObjectMapper();
			String jsonInString = mapper.writeValueAsString(user);
			logger.info("Updating User " + user.USERID);
			collection.replaceOne(new Document().append("userid", user.USERID), Document.parse(jsonInString) );
		}
		catch(com.mongodb.MongoSocketOpenException|com.mongodb.MongoTimeoutException e)
		{
			System.out.println("Could not connect to the database");
			logger.error("DBNotAvailable");
			logger.error(e);
			throw new Exception("DBNotAvailable");
		} catch (JsonProcessingException e) {
			logger.error("DBNotAvailable");
			logger.error(e);
			throw new Exception("DBNotAvailable");
		}		
	}

	private OTP GenerateOTP(User newUser) throws IOException {
		String OTP;
		int buffer; 
		Random randomNumberGenerator = new Random();
		OTP = java.lang.Math.abs(randomNumberGenerator.nextInt() % 9999)  + "";
		while(OTP.length()<4)
			OTP = "0" + OTP;
		OTP otp = new OTP();
		otp.setOtp(OTP);
		otp.setGenerationDate(LocalDateTime.now());
		return otp ;
	}

	private int GenerateUserID(User newUser) {
		String phoneNumber = newUser.getPhoneNumber();
		int userID = 0;
		int alternator = 0;
		for(char c : phoneNumber.toCharArray())
		{if(alternator%3==0) userID = Integer.parseInt(""+userID+c); alternator+= 1;}
		try {
			while(true){
				if(getUser(userID) == null) return userID;
				else userID += 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	private User isValidUser(String key,String object) throws Exception {
		MongoCollection<?> collection = APIEndpointRecievers.getDatabase().getCollection(ApplicationConstants.USERS_TABLE);
		Document myDoc = (Document) collection.find(eq(key, object)).first();
		if(myDoc==null) {
			System.out.println("User not registered");
			return null;
		}
		logger.info("User with ID " + key +" present");
		User user = new User();
		ObjectMapper mapper = new ObjectMapper();
		myDoc.remove("_id");
		//			System.out.println(myDoc.toJson());
		user = mapper.readValue(myDoc.toJson(),User.class);
		return user;
	}

	private User getUser(int userID) throws MongoException, UserNotFoundException,IOException {
		try {
			MongoCollection<?> collection = APIEndpointRecievers.getDatabase().getCollection(ApplicationConstants.USERS_TABLE);
			Instant startTime = Instant.now();
			Document myDoc = (Document) collection.find(eq(ApplicationConstants.USER_ID_COLUMN_NAME, userID)).first();
			logger.log(Level.INFO,"MongoQuery for user took " + Duration.between(startTime, Instant.now()).toMillis());
			if(myDoc==null) {
				logger.log(Level.WARN,"User with id "+ userID +" not registered");
//				System.out.println("User not registered");
				throw new UserNotFoundException("Failed to locate user.");
//				return null;
			}
			logger.info("DB Query for userID " + userID +" successful. ");
			User user = new User();
			ObjectMapper mapper = new ObjectMapper();
			myDoc.remove("_id");
			//			System.out.println(myDoc.toJson());
			user = mapper.readValue(myDoc.toJson(),User.class);
			return user;
		}
		catch(com.mongodb.MongoSocketOpenException|com.mongodb.MongoTimeoutException e)
		{
			//				System.out.println("Could not connect to the database");
			logger.error("DataBase Connection Failed");
			logger.error(e);
			throw new MongoException("DBNotAvailable");
		}catch (IOException e) {
			logger.error("Corrupted Data for user " + userID);
			logger.error(e);
			throw new IOException("CorruptedData");
		}
	}
	
	public static void sendSMS(String messageUnencoded, String phoneNumber) throws IOException{
		final String USER_AGENT = "Mozilla/5.0";
		String url = "https://control.msg91.com/api/sendhttp.php?";
		String authKey = "authkey=133074ArSpCEmYY5846eb3e";
		String mobiles = "mobiles= " + "91" + phoneNumber;//
		String message = "message=" + messageUnencoded.replace(" ", "%20" );
		String sender = "sender=KNWALT";
		String route= "route=4";
		String delim = "&";

		List<String> parameters = new ArrayList<>();
		parameters.add(authKey);
		parameters.add(mobiles);
		parameters.add(message);
		parameters.add(sender);
		parameters.add(route);

		String URLParams = String.join("&", parameters);
		URL obj = new URL(url + URLParams);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
	}
}