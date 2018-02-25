package CoinMonitor.APIService;

import static com.mongodb.client.model.Filters.eq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

//import org.apache.logging.log4j.Logger;

import CoinMonitor.APIService.Currency.CurrencySnapShot;

import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
@Configuration
@EnableScheduling
public class PriceUpdater {
	
	public static boolean initUser = true;
	public static User user1;
	public static Logger logger = LogManager.getFormatterLogger(PriceUpdater.class);
	static MongoCollection<Document> priceHistory = APIEndpointMapper.getDatabase().getCollection("PriceHistory");
	
	
//	@Autowired mainClass classA;
//	@Bean
	
	@Scheduled(fixedDelay = 10000)
	public void RefreshRateV2(){
		try {

			URL url = new URL("https://api.coinmarketcap.com/v1/ticker/?convert=INR");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			String output;
			String JSONString = "";
//			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				JSONString += output;
			}
			conn.disconnect();
			JSONArray currencyList = new JSONArray(JSONString.trim());
			for(int i=0; i < currencyList.length(); i++)
			{
				JSONObject currency = currencyList.getJSONObject(i);
				Currency c = Currency.getCURRENCYSTATE().get("symbol");
				CurrencySnapShot thisValue;
				if(c!= null)
				{
					CurrencySnapShot currencyValue = new CurrencySnapShot(
																			Float.parseFloat((String)currency.get("price_inr")), 
																			Float.parseFloat((String)currency.get("price_usd")), 
																			System.currentTimeMillis()/1000,
																			c.getCurrencyCode());
					thisValue = currencyValue;
					Currency.updateCurrencyValue((String)currency.get("symbol"), currencyValue);
				}
				else
				{
					Currency newCurrency = new Currency();
					newCurrency.setCurrencyCode((String)currency.get("symbol"));
					newCurrency.setName((String)currency.get("name"));
					CurrencySnapShot value = new CurrencySnapShot(
																	Float.parseFloat((String)currency.get("price_inr")), 
																	Float.parseFloat((String)currency.get("price_usd")), 
																	System.currentTimeMillis()/1000,
																	newCurrency.getCurrencyCode());
					thisValue = value;
					newCurrency.setValue(value);
					HashMap<String,CurrencySnapShot> currencyHistory = new HashMap<String,CurrencySnapShot>();
					currencyHistory.put(LocalDateTime.now().toString(),value);
					newCurrency.setHistory(currencyHistory);
					Currency.makeNewCurrency(newCurrency);
				}
				
				try{
				this.priceHistory.insertOne(Document.parse(thisValue.getSimplifiedJSONString()));}
				catch(Exception e){
					logger.error("Updating the priceHistory Failed");
				}
				
				try{
					if(thisValue.getCurrencyCode() == "XRP")
					for( Document myDoc :priceHistory.find(eq("code","XRP"))){
					CurrencySnapShot thisSnap = new CurrencySnapShot();
					ObjectMapper mapper = new ObjectMapper();
					System.out.println(myDoc.toJson());
					}
				}
				catch (Exception e){
					System.out.println(e.getMessage());
				}
				
				
			}
			if(!Currency.getCURRENCYSTATE().containsKey("INR")){
				Currency newCurrency = new Currency();
				newCurrency.setCurrencyCode("INR");
				newCurrency.setName("Rupee");
				CurrencySnapShot value = new CurrencySnapShot(1.0f, 1/65f, System.currentTimeMillis()/1000,newCurrency.getCurrencyCode());
				newCurrency.setValue(value);
				HashMap<String,CurrencySnapShot> currencyHistory = new HashMap<String,CurrencySnapShot>();
				currencyHistory.put(LocalDateTime.now().toString(),value);
				newCurrency.setHistory(currencyHistory);
				
			}
			
		  } catch (IOException e) {

			e.printStackTrace();

		  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args){
//		MongoDatabase db = ServiceController.getDatabase();
		MongoClient mc = new MongoClient("192.168.0.126:27017");
		MongoDatabase mdb = mc.getDatabase("CoinMonitor");
		MongoCollection<Document> collection = mdb.getCollection("Users");
		PriceUpdater r = new PriceUpdater();
//		r.RefreshState();
		User user = r.user1;
		System.out.println(user);
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString;
		
		//This is the block to check if the user exists
		try{
			Document myDoc = collection.find(eq("userID", user.USERID)).first();
			if(myDoc==null) {
				try {
					jsonInString = mapper.writeValueAsString(user);
					collection.insertOne(Document.parse(jsonInString));
				} catch (JsonProcessingException e) {
					System.out.println("check");
				}
			}
			else{
			System.out.println("Sorry bro UserID exists");
			}
//			logger.info("User with ID " + userID +" present");
		}
		catch(Exception e){
			System.out.println("DB Down lag raha hai bro");
		}
	
		
	}

}
