package CoinMonitor.APIService;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import CoinMonitor.APIService.Currency.CurrencySnapShot;

@Controller
public class ServiceController {
	public final static Logger logger = LogManager.getLogger(ServiceController.class);
	private static MongoClient mongoClient;
	private static MongoDatabase database;
	
	public static MongoClient getMongoClient() {
		return mongoClient;
	}

	public static void setMongoClient(MongoClient mongoClient) {
		ServiceController.mongoClient = mongoClient;
	}

	public static MongoDatabase getDatabase() {
		if(database == null)
			setDataBase();
		return ServiceController.database;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setDataBase()
	{
		if(ServiceController.mongoClient == null)
			ServiceController.mongoClient = new MongoClient();
		ServiceController.database = mongoClient.getDatabase(ApplicationConstants.DATABASE);
		
	}

	@RequestMapping(path="/Trade",method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody String Trade(HttpServletRequest Request, HttpServletResponse response, @RequestBody String jsonString){
		try{
			JSONParser parser = new JSONParser();
			JSONObject newJObject = null;
			newJObject = (JSONObject) parser.parse(jsonString);
			int userID = Integer.parseInt("" + newJObject.get("userID"));
			ObjectMapper mapper = new ObjectMapper();
			User user = getUser(userID);
			String currencyCode = (String) newJObject.get("currencyCode");
			float quantity = Float.parseFloat("" + newJObject.get("quantity"));
			float price = Float.parseFloat("" + newJObject.get("price"));
			Transaction transaction;
			if(quantity > 0)
				transaction = new Transaction(Currency.getCURRENCYSTATE().get("INR"), Currency.getCURRENCYSTATE().get(currencyCode), price, quantity);
			else
				transaction = new Transaction(Currency.getCURRENCYSTATE().get(currencyCode), Currency.getCURRENCYSTATE().get("INR"), 1/price, quantity*price);
			user.trade(transaction);
			updateUser(user);
			return ""+true;
		}
		catch(Exception e){
			return ""+false;
		}
	}
	
	@RequestMapping(path="/getWatchList" , method = RequestMethod.POST)
	public @ResponseBody String getWatchList(HttpServletRequest Request, HttpServletResponse response, @RequestBody String jsonString){
		try{
			JSONParser parser = new JSONParser();
			JSONObject bufferJSONObject = null;
			bufferJSONObject = (JSONObject) parser.parse(jsonString);
			int userID = Integer.parseInt("" + bufferJSONObject.get("userID"));
//			ObjectMapper mapper = new ObjectMapper();
//			User user = getUser(userID);
			List<Currency> watchList = getUser(userID).getWatchList();
			bufferJSONObject = new JSONObject();
			String s;
			for(Currency c:watchList)
			{
				s = (Currency.getCURRENCYSTATE().get(c.currencyCode)).getValue().toJSONString();
				bufferJSONObject.put(c.currencyCode, s);
			}
			return bufferJSONObject.toJSONString();
		}
		catch(Exception e){
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
			if(watchList.contains(Currency.getCURRENCYSTATE().get(currencyCode))){
				watchList.remove(Currency.getCURRENCYSTATE().get(currencyCode));
			}
			else{
				watchList.add(Currency.getCURRENCYSTATE().get(currencyCode));
			}
			
			user.setWatchList(watchList);
			updateUser(user);
			
//			bufferJSONObject = new JSONObject();
//			String s;
//			for(Currency c:watchList)
//			{
//				s = (Currency.getCURRENCYSTATE().get(c.currencyCode)).getValue().toJSONString();
//				bufferJSONObject.put(c.currencyCode, s);
//			}
			return ""+true;
		}
		catch(Exception e){
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
	
	@RequestMapping(path="/add",method={RequestMethod.POST,RequestMethod.GET}, produces = "application/json", consumes = "application/json")
	public @ResponseBody String add(HttpServletRequest Request, HttpServletResponse response,@RequestBody String jsonString) throws Exception{

		System.out.println(jsonString);
		JSONParser parser = new JSONParser();
		
		//convert from JSON string to JSONObject
		JSONObject newJObject = null;
		try {
			newJObject = (JSONObject) parser.parse(jsonString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		System.out.println(newJObject.get("walletsection"));
		ObjectMapper mapper = new ObjectMapper();
		WalletSection wallet = mapper.readValue(newJObject.get("walletsection").toString(), WalletSection.class);
		
		System.out.println(newJObject.get("userID"));
		System.out.println(newJObject.get("price"));
		System.out.println(newJObject.get("volume"));
		System.out.println(newJObject.get("total"));
		return "baba";
	}
	
	@RequestMapping(path="/SellCurrency",params = {"userID", "PIN"})
	public @ResponseBody String SellCurrency(HttpServletRequest Request, HttpServletResponse response,  @RequestParam(value = "userID") String userIDString,@RequestParam(value = "PIN") String PIN){
		int userID = Integer.parseInt(userIDString);
		try{			
		User user = getUser(userID);
		String currencyCode = null ;
		CurrencySnapShot purchasePrice = new CurrencySnapShot(0, 0, null);
		float numberOfCoins = 0;
		float sellPriceINR = 0;
		float sellPriceUSD = 0;
		LocalDateTime purchaseDate = null;
		Currency currency = new Currency();
		WalletSection holding = new WalletSection(currency, numberOfCoins, purchaseDate, purchasePrice);
		Transaction transaction = new Transaction();
		user.trade(transaction);
		updateUser(user);
		}
		catch(Exception e){
			return null;
		}
		return PIN;
	}
	@RequestMapping(path="/getBalance",params = {"userID", "PIN"})
	public @ResponseBody String getBalance(HttpServletRequest Request, HttpServletResponse response,  @RequestParam(value = "userID") String userIDString,@RequestParam(value = "PIN") String PIN){
		int userID = Integer.parseInt(userIDString);
		try{
			User user = getUser(userID);
			float balance = user.wallet.getCurrentValue();
			return PIN;
		}
		catch(Exception e){
			return null;
		}
		
	}
	@RequestMapping(path="/getCurrentState",params = {"userID", "PIN"})
	public @ResponseBody User getCurrentState(HttpServletRequest Request, HttpServletResponse response,  @RequestParam(value = "userID") String userIDString,@RequestParam(value = "PIN") String PIN){
		int userID = Integer.parseInt(userIDString);
		try{
			User user = getUser(userID);
		HashMap<String,Float> HoldingsbyCurrency = new HashMap<>();
		for(WalletSection h:user.wallet.sections.values())
		{
			if(HoldingsbyCurrency.get(h.currency.currencyCode) == null || HoldingsbyCurrency.get(h.currency.currencyCode) == 0)
				HoldingsbyCurrency.put(h.currency.currencyCode,h.currentBalance);
			else
				HoldingsbyCurrency.put(h.currency.currencyCode,HoldingsbyCurrency.get(h.currency.currencyCode) + h.currentBalance);
		}
		String output = "You hold ";
		float totalValue = 0;
		for(String s:HoldingsbyCurrency.keySet())
		{
			float coinValue = HoldingsbyCurrency.get(s)*((Currency)Currency.CURRENCYSTATE.get(s)).value.valueInUSD;
			output +=  HoldingsbyCurrency.get(s) + " of " + s + " currently valued at " + ((Currency)Currency.CURRENCYSTATE.get(s)).value.valueInUSD + " making your investment worth " + coinValue +"\n" ;
			totalValue += coinValue;
		}
			output += " Your total investments are worth "+totalValue;
			return user;
		}
		catch(Exception e){
			return null;
		}
	}
	
	
	private void updateUser(User user) throws Exception {
		try{
		MongoCollection<Document> collection = ServiceController.getDatabase().getCollection("Users");
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

	private User getUser(int userID) throws Exception {
		try {
			MongoCollection collection = ServiceController.getDatabase().getCollection(ApplicationConstants.USERS_TABLE);
			Document myDoc = (Document) collection.find(eq(ApplicationConstants.USER_ID_COLUMN_NAME, userID)).first();
			
			if(myDoc==null) {
				System.out.println("User not registered");
				return null;
			}
			logger.info("User with ID " + userID +" present");
			User user = new User();
			ObjectMapper mapper = new ObjectMapper();
			myDoc.remove("_id");
			System.out.println(myDoc.toJson());
			user = mapper.readValue(myDoc.toJson(),User.class);
			return user;
			}
			catch(com.mongodb.MongoSocketOpenException|com.mongodb.MongoTimeoutException e)
			{
				System.out.println("Could not connect to the database");
				throw new Exception("DBNotAvailable");
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("DBNotAvailable");
				logger.error(e);
				throw new Exception("DBNotAvailable");
			}
		return null;
	}
	
private User getabc() throws Exception{
	User user1 = new User();
	
//	if(initUser)
	{
		
		user1.setEmailID("sanchitgarg2@gmail.com");
		user1.setLiquidCashInWallet(10f);
		user1.setPhoneNumber("8147325346");
		user1.setUSERID(10);
		
		
		CoinWallet wallet = new CoinWallet();
		Currency currency1 = new Currency();
		
		//XRP
		currency1.setCurrencyCode("XRP");
		currency1.setName("Ripple");
		currency1.setValue(new CurrencySnapShot(200.0f, 0, LocalDateTime.now().toString()));
		HashMap<String,CurrencySnapShot> XRPHistory = new HashMap<String,CurrencySnapShot>();
		XRPHistory.put(LocalDateTime.now().toString(),new CurrencySnapShot(200.0f, 0, LocalDateTime.now().toString()));
		currency1.setHistory(XRPHistory);
		Currency.makeNewCurrency(currency1);
		
		//SUB
		currency1 = new Currency();
		currency1.setCurrencyCode("SUB");
		currency1.setName("Substratum");
		currency1.setValue(new CurrencySnapShot(40.0f, 0, LocalDateTime.now().toString()));
		XRPHistory = new HashMap<String,CurrencySnapShot>();
		XRPHistory.put(LocalDateTime.now().toString(),new CurrencySnapShot(38.0f, 0, LocalDateTime.now().toString()));
		currency1.setHistory(XRPHistory);
		Currency.makeNewCurrency(currency1);
		
		//ETH
		
		currency1 = new Currency();
		currency1.setCurrencyCode("ETH");
		currency1.setName("Etherium");
		currency1.setValue(new CurrencySnapShot(100000.0f, 0, LocalDateTime.now().toString()));
		XRPHistory = new HashMap<String,CurrencySnapShot>();
		XRPHistory.put(LocalDateTime.now().toString(),new CurrencySnapShot(92000.0f, 0, LocalDateTime.now().toString()));
		currency1.setHistory(XRPHistory);
		Currency.makeNewCurrency(currency1);
		
		//TRX
		
		currency1 = new Currency();
		currency1.setCurrencyCode("TRX");
		currency1.setName("Tron");
		currency1.setValue(new CurrencySnapShot(6.0f, 0, LocalDateTime.now().toString()));
		XRPHistory = new HashMap<String,CurrencySnapShot>();
		XRPHistory.put(LocalDateTime.now().toString(),new CurrencySnapShot(8.0f, 0, LocalDateTime.now().toString()));
		currency1.setHistory(XRPHistory);
		Currency.makeNewCurrency(currency1);
		
		//XLM
		
		currency1 = new Currency();
		currency1.setCurrencyCode("XLM");
		currency1.setName("Stellar Lumens");
		currency1.setValue(new CurrencySnapShot(15.0f, 0, LocalDateTime.now().toString()));
		XRPHistory = new HashMap<String,CurrencySnapShot>();
		XRPHistory.put(LocalDateTime.now().toString(),new CurrencySnapShot(26.0f, 0, LocalDateTime.now().toString()));
		currency1.setHistory(XRPHistory);
		Currency.makeNewCurrency(currency1);
		
		//XVG
		
		currency1 = new Currency();
		currency1.setCurrencyCode("XVG");
		currency1.setName("Verge");
		currency1.setValue(new CurrencySnapShot(26.0f, 0, LocalDateTime.now().toString()));
		XRPHistory = new HashMap<String,CurrencySnapShot>();
		XRPHistory.put(LocalDateTime.now().toString(),new CurrencySnapShot(31.0f, 0, LocalDateTime.now().toString()));
		currency1.setHistory(XRPHistory);
		Currency.makeNewCurrency(currency1);
		
		Currency INR = new Currency();
		INR.setCurrencyCode("INR");
		INR.setName("Rupee");
		INR.setValue(new CurrencySnapShot(1.0f, 0, LocalDateTime.now().toString()));
		INR.setValue(new CurrencySnapShot(1.0f, 0, LocalDateTime.now().toString()));
		HashMap<String,CurrencySnapShot> INRHistory = new HashMap<String,CurrencySnapShot>();
		INRHistory.put(LocalDateTime.now().toString(),new CurrencySnapShot(1.0f, 0, LocalDateTime.now().toString()));
		INR.setHistory(INRHistory);
		Currency.makeNewCurrency(INR);	
		
		WalletSection section;
		for(Currency c: Currency.CURRENCYSTATE.values())
		{
			 section = new WalletSection(c,c.getValue().valueInINR,LocalDateTime.now(),c.getValue());
			 wallet.addNewSection(section);
		}
		
		user1.setWallet(wallet);
		
	}
	return user1;
}
	

}
