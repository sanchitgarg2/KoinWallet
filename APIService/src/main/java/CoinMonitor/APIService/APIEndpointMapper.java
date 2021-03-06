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
import java.util.Iterator;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import CoinMonitor.APIService.Exceptions.UserNotFoundException;

@Controller
@SuppressWarnings("unchecked")
public class APIEndpointMapper {
	public APIEndpointMapper() {
		super();
		System.out.println("Setting MongoDB Driver Logging to SEVERE");
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(java.util.logging.Level.SEVERE);

	}

	public final static Logger logger = LogManager.getLogger(APIEndpointMapper.class);
	private static MongoClient mongoClient;
	private static MongoDatabase database;

	public static MongoClient getMongoClient() {
		if(mongoClient == null)
			initializeMongoClient();
		return mongoClient;
	}
	public static void setMongoClient(MongoClient mongoClient) {
		APIEndpointMapper.mongoClient = mongoClient;
	}
	public static MongoDatabase getDatabase() {
		if(database == null)
			initializeMongoClient();
		return APIEndpointMapper.database;
	}
	public static Logger getLogger() {
		return logger;
	}
	public static void initializeMongoClient()
	{
		logger.log(Level.INFO,"Initializing MongoClient.");
		if(APIEndpointMapper.mongoClient == null){
			try{
				APIEndpointMapper.mongoClient = new MongoClient("localhost:27017");
			}
			catch(Exception e){
				logger.error("Error in Initialzing Mongo Client");
				throw e;
			}
		}
		APIEndpointMapper.database = mongoClient.getDatabase(ApplicationConstants.DATABASE);
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
				transaction = new Transaction(Currency.getStaticCurrencyState().get("INR"), Currency.getStaticCurrencyState().get(currencyCode), price, quantity);
			else if(quantity < 0)
				transaction = new Transaction(Currency.getStaticCurrencyState().get(currencyCode), Currency.getStaticCurrencyState().get("INR"), 1/price, quantity*price);
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
			JSONObject currencyListJson = new JSONObject();
			for (Currency c : watchList) {
				try {
					s = (Currency.getCurrency(c.getCurrencyCode())).getValue().getJSONString();
					currencyListJson.put(c.getCurrencyCode(), s);
				} catch (Exception e) {
					logger.error("Currency " + c + "in WatchList, but does not exist.");
				}

			}
			logger.info("Served Request successfully.");
			bufferJSONObject.put("currencyList", currencyListJson.toJSONString());
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
			newUser.setWallet(new Wallet());
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
		catch (MongoException e) {
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

	@RequestMapping(path="/Login" , method = RequestMethod.POST)
	public @ResponseBody String Login(HttpServletRequest Request, HttpServletResponse response, @RequestBody String jsonString){
		JSONObject returnObject;
		try{
			JSONParser parser = new JSONParser();
			JSONObject bufferJSONObject = null;
			bufferJSONObject = (JSONObject) parser.parse(jsonString);
			//TODO:Use the below values or delete them.
			String email = null;
			String phoneNumber = null;
			returnObject = new JSONObject();
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
							returnObject.put("currencyList",((JSONObject) new JSONParser().parse(getCurrencyList(null, null))).get("currencyList"));
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
						ObjectMapper m = new ObjectMapper();
						returnObject.put("status", "Successful");
						returnObject.put("statusCode", 200);
						returnObject.put("user", m.writeValueAsString(getUser(user.getUSERID())));
						returnObject.put("currencyList", ((JSONObject) new JSONParser().parse(getCurrencyList(null, null))).get("currencyList"));
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
		}
		catch (MongoException e) {
			returnObject = new JSONObject();
			returnObject.put("status", "DataBase is down.");
			returnObject.put("statusCode", 550);
			logger.error(e);
			return returnObject.toJSONString();
		} catch (IOException e) {
			returnObject = new JSONObject();
			returnObject.put("status", "Corrupted Data");
			returnObject.put("statusCode", 551);
			logger.error(e);
			return returnObject.toJSONString();
		} catch (Exception e) {
			returnObject = new JSONObject();
			returnObject.put("status", "App Server is facing an Internal issue");
			returnObject.put("statusCode", 500);
			logger.error(e);
			return returnObject.toJSONString();
		}
	}

	@RequestMapping(path="/getCurrencyList" , method = RequestMethod.GET)
	public @ResponseBody String getCurrencyList(HttpServletRequest Request, HttpServletResponse response){
		JSONObject bufferJSONObject = new JSONObject();
		bufferJSONObject.put("currencyList",null);
		try{
			JSONObject bufferJSONObjectCurrencyList = new JSONObject();
			String s;
			
			for(Currency c:Currency.getStaticCurrencyState().values())
			{
				try{
					s = (Currency.getStaticCurrencyState().get(c.getCurrencyCode())).getValue().getJSONString();
					bufferJSONObjectCurrencyList.put(c.getCurrencyCode(), s);}
				catch(Exception e){
					logger.error(this.getClass() + ".getCurrencyList" +"Something wrong here" + e.getMessage());
				}
			}
			bufferJSONObject.put("currencyList", bufferJSONObjectCurrencyList.toJSONString());
			bufferJSONObject.put("status", "Successful.");
			bufferJSONObject.put("statusCode", 200);
			return bufferJSONObject.toJSONString();
		}
		catch(NumberFormatException e){
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
		catch(Exception e){
			bufferJSONObject = new JSONObject();
			bufferJSONObject.put("status", "App Server has an Internal error.");
			bufferJSONObject.put("statusCode", 500);
			logger.error(e);
			return bufferJSONObject.toJSONString();
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
			if(watchList.contains(Currency.getStaticCurrencyState().get(currencyCode)))
				watchList.remove(Currency.getStaticCurrencyState().get(currencyCode));
			else watchList.add(Currency.getStaticCurrencyState().get(currencyCode));
			user.setWatchList(watchList);
			updateUser(user);
			return ""+true;
		}
		catch(Exception e){
			logger.error("Update WatchList Failed with Error " + e.getMessage() , jsonString , Currency.getStaticCurrencyState());
			return ""+false;
		}
	}

	@RequestMapping(path="/getUpdate" , method = RequestMethod.GET)
	public @ResponseBody String getCurrencyState() throws Exception{
		JSONObject JsonObject = new JSONObject();
		String s = "";
		for (Currency c : Currency.getStaticCurrencyState().values()){
			s = c.getValue().getJSONString();
			JsonObject.put(c.getCurrencyCode(), s);
		}
		return JsonObject.toJSONString();
	}

	@RequestMapping(path="/getUser",params = {"userID"})
	public @ResponseBody String getUser(HttpServletRequest Request, HttpServletResponse response,  @RequestParam(value = "userID") String userIDString){
		JSONObject returnObject = new JSONObject();
		ObjectMapper m = new ObjectMapper();
		int userID = Integer.parseInt(userIDString);
		try {
			String s = m.writeValueAsString(getUser(userID));
			returnObject.put("user", s);
			returnObject.put("status", "Successful.");
			returnObject.put("statusCode", 200);
			return returnObject.toJSONString();
		} catch (MongoException e) {
			returnObject = new JSONObject();
			returnObject.put("status", "DataBase is down.");
			returnObject.put("statusCode", 550);
			logger.error(e);
			return returnObject.toJSONString();
		} catch (IOException e) {
			returnObject = new JSONObject();
			returnObject.put("status", "Corrupted Data");
			returnObject.put("statusCode", 551);
			logger.error(e);
			return returnObject.toJSONString();
		} catch (Exception e) {
			returnObject = new JSONObject();
			returnObject.put("status", "App Server is facing an Internal issue");
			returnObject.put("statusCode", 500);
			logger.error(e);
			return returnObject.toJSONString();

		}
	}
	
	//TODO:Set rules for the DB collections. Create validator functions. 
	//TODO:Storing different timelines in different Collections vs Same Collection as a field.
	
	@RequestMapping(path="/getCandleStickData", method = RequestMethod.POST)
	public @ResponseBody String getCandleStickData(HttpServletRequest Request, HttpServletResponse response,@RequestBody String jsonString){
		ArrayList<CandleStickDataPoint> graphData = new ArrayList<>();
		MongoCollection<Document> candleStickData = APIEndpointMapper.getDatabase().getCollection("CandleStickData");
		JSONParser parser = new JSONParser();
		JSONObject returnObject = null;
		
		String currencyCode;
		String timeLine;

		try{
			returnObject = (JSONObject) parser.parse(jsonString);
			timeLine = "" + returnObject.get("timeLine");
			currencyCode = (String)returnObject.get("currencyCode");
			JSONObject bac = new JSONObject();
			JSONObject query = new JSONObject();
			query.put("currencyCode",new Document("$eq",currencyCode ));
			query.put("timeLine", new Document("$eq",TimeLinetoPulseConverter.getMapping(timeLine)));
			StringBuilder myString = new StringBuilder();
			myString.append(" \"openTimeStamp\" : " + " { " + " \"$gt\" : " + TimeLinetoPulseConverter.getStartTime(timeLine)+" }" );
//			bac.put("$gt" , );
//			query.put("openTimeStamp", bac.toJSONString());
			String abce = query.toJSONString();
			abce = abce.substring(0,abce.length()-1)+ " ," + myString.toString() + " }";
			//		graphData = new ArrayList<>();
			FindIterable<Document> abc  = candleStickData.find(Document.parse(abce)).sort(new Document("odate",1));
			//Sort the data to be most recent first.
			List dataPoints = new ArrayList<Document>();
			int numberOfGraphDataPoints = ApplicationConstants.CANDLE_STICK_DATA_POINTS;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			CandleStickDataPoint dataPoint;
			ArrayList<CandleStickDataPoint> returnData = new ArrayList<CandleStickDataPoint>();
			returnObject = new JSONObject();
			for( Document myDoc : abc ){
//				myDoc.remove("_id");
				dataPoint = mapper.readValue(myDoc.toJson(),CandleStickDataPoint.class);
				graphData.add(dataPoint);
			}
			if(graphData.size() == 0){
//				returnObject.put("graphData", dataJson);
				returnObject.put("status", "Successful.");
				returnObject.put("statusCode", 200);
				//TODO:No data found. Generate data and re-call this service.
			}
			else{
				//TODO:Check if the last element has a timeStamp more than 5% before the  currentTime;
				int lowerBound = graphData.size() / numberOfGraphDataPoints;
				int higherBound = lowerBound + 1;
				int moduloOperator = numberOfGraphDataPoints / (graphData.size() % numberOfGraphDataPoints);
				Iterator<CandleStickDataPoint> inputData = graphData.iterator();
				for(int i=0;i<numberOfGraphDataPoints&&inputData.hasNext();i++)
				{
					CandleStickDataPoint thisPoint = new CandleStickDataPoint();
					CandleStickDataPoint inputPoint;
					if(i%moduloOperator == 0){
						for(int j=0; inputData.hasNext() && j <= higherBound ; j++){
							inputPoint = inputData.next();
							thisPoint.merge(inputPoint);
						}
					}
					else{
						for(int j=0; inputData.hasNext() && j <= lowerBound ; j++){
							inputPoint = inputData.next();
							thisPoint.merge(inputPoint);
						}
					}
					returnData.add(thisPoint);
				}
				String dataJson = (new ObjectMapper()).writeValueAsString(returnData);
				returnObject.put("graphData", dataJson);
				returnObject.put("status", "Successful.");
				returnObject.put("statusCode", 200);

			}
			return returnObject.toJSONString();
		}
		catch(Exception e){
			logger.error(e);
			//TODO:Add status codes for the error state to the response sent.
		}
		return null;
	}

	private void createUser(User user) throws Exception {
		MongoCollection<Document> collection = APIEndpointMapper.getDatabase().getCollection("Users");
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(user);
		collection.insertOne(Document.parse(jsonInString));
	}

	private void updateUser(User user) throws Exception {
		try{
			MongoCollection<Document> collection = APIEndpointMapper.getDatabase().getCollection("Users");
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
		MongoCollection<?> collection = APIEndpointMapper.getDatabase().getCollection(ApplicationConstants.USERS_TABLE);
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
			MongoCollection<?> collection = APIEndpointMapper.getDatabase().getCollection(ApplicationConstants.USERS_TABLE);
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
		List<String> urlParameters = new ArrayList<>();

		urlParameters.add(authKey);
		urlParameters.add(mobiles);
		urlParameters.add(message);
		urlParameters.add(sender);
		urlParameters.add(route);

		String URLParams = String.join("&", urlParameters);
		URL obj = new URL(url + URLParams);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		//		System.out.println("\nSending 'GET' request to URL : " + url);
		//		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		//
		//		//print result
		//		System.out.println(response.toString());
	}
}