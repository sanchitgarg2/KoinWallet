package CoinMonitor.APIService;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import CoinMonitor.APIService.Currency.CurrencySnapShot;


public class ServiceController {
	public final static Logger logger = Logger.getLogger(ServiceController.class);
	private static MongoClient mongoClient;
	private static MongoDatabase database;
	
	public static void setDataBase()
	{
		if(ServiceController.mongoClient == null)
			ServiceController.mongoClient = new MongoClient();
		ServiceController.database = mongoClient.getDatabase("CoinMonitor");
		
	}

	@RequestMapping(path="/Trade",params = {"userID"}, method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public @ResponseBody Boolean Trade(HttpServletRequest Request, HttpServletResponse response, @RequestBody Transaction transcaction ,@RequestParam(value = "userID") String userID){
		try{
			User user = getUser(userID);
			Transaction transaction = new Transaction();
			user.trade(transaction);
			updateUser(user);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	@RequestMapping(path="/SellCurrency",params = {"userID", "PIN"})
	public @ResponseBody String SellCurrency(HttpServletRequest Request, HttpServletResponse response,  @RequestParam(value = "userID") String userID,@RequestParam(value = "PIN") String PIN){
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
		return null;
		}
		catch(Exception e){
			return null;
		}
	}
	@RequestMapping(path="/getBalance",params = {"userID", "PIN"})
	public @ResponseBody String getBalance(HttpServletRequest Request, HttpServletResponse response,  @RequestParam(value = "userID") String userID,@RequestParam(value = "PIN") String PIN){
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
	public @ResponseBody User getCurrentState(HttpServletRequest Request, HttpServletResponse response,  @RequestParam(value = "userID") String userID,@RequestParam(value = "PIN") String PIN){
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
			float coinValue = HoldingsbyCurrency.get(s)*((Currency)Currency.CURRENCYSTATE.get(s)).Value.valueInUSD;
			output +=  HoldingsbyCurrency.get(s) + " of " + s + " currently valued at " + ((Currency)Currency.CURRENCYSTATE.get(s)).Value.valueInUSD + " making your investment worth " + coinValue +"\n" ;
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
		MongoCollection<Document> collection = this.database.getCollection("Users");
		Document myDoc = collection.find(eq("userID", user.USERID)).first();
		if(myDoc==null) {
			System.out.println("User not registered");
		}
		logger.info("User with ID " + user.USERID +" present");		
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(user);
		logger.info("Updating User " + user.USERID);
		collection.insertOne(Document.parse(jsonInString));
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

	private User getUser(String userID) throws Exception {
		try {
			MongoCollection<Document> collection = this.database.getCollection("Users");
			Document myDoc = collection.find(eq("userID", userID)).first();
			if(myDoc==null) {
				System.out.println("User not registered");
				return null;
			}
			logger.info("User with ID " + userID +" present");
			User user = new User();
			ObjectMapper mapper = new ObjectMapper();
			user = (User)mapper.readValue(myDoc.toJson(),User.class);
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
	

}
