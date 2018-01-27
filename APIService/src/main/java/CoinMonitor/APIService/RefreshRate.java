package CoinMonitor.APIService;

import static com.mongodb.client.model.Filters.eq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
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

//import org.apache.logging.log4j.Logger;

import CoinMonitor.APIService.Currency.CurrencySnapShot;

import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
@Configuration
@EnableScheduling
public class RefreshRate {
	
	public static boolean initUser = true;
	public static User user1;
	public static Logger logger = LogManager.getFormatterLogger(RefreshRate.class);
	
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
				if(Currency.getCURRENCYSTATE().keySet().contains((String)currency.get("symbol")))
				{
					CurrencySnapShot currencyValue = new CurrencySnapShot(Float.parseFloat((String)currency.get("price_inr")), Float.parseFloat((String)currency.get("price_usd")), LocalDateTime.now().toString());
					Currency.updateCurrencyValue((String)currency.get("symbol"), currencyValue);
				}
				else
				{
					Currency newCurrency = new Currency();
					newCurrency.setCurrencyCode((String)currency.get("symbol"));
					newCurrency.setName((String)currency.get("name"));
					newCurrency.setValue(new CurrencySnapShot(Float.parseFloat((String)currency.get("price_inr")), Float.parseFloat((String)currency.get("price_usd")), LocalDateTime.now().toString()));
					HashMap<String,CurrencySnapShot> currencyHistory = new HashMap<String,CurrencySnapShot>();
					currencyHistory.put(LocalDateTime.now().toString(),new CurrencySnapShot(8.0f, 0, LocalDateTime.now().toString()));
					newCurrency.setHistory(currencyHistory);
					Currency.makeNewCurrency(newCurrency);
				}
				
			}
			
		  } catch (IOException e) {

			e.printStackTrace();

		  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
//	@Scheduled(fixedDelay = 10000)
	public void RefreshState() {
		
		try {
			Transaction t = new Transaction();
			user1 = new User();
			
//			if(initUser)
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
				initUser = false;
				
			}
			
			
//			WalletSection section = new WalletSection(XRP,75.0f,LocalDateTime.now().toString(),new CurrencySnapShot(200.0f, 0, LocalDateTime.now().toString()));
//			WalletSection sectionINR = new WalletSection(INR,75.0f,LocalDateTime.now().toString(),new CurrencySnapShot(1.0f, 0, LocalDateTime.now().toString()));
			
			
//			wallet.addNewSection(section);
//			wallet.addNewSection(sectionINR);
			
			
			t.setIncomingCurrency(Currency.CURRENCYSTATE.get("XRP"));
//			System.out.println(Currency.CURRENCYSTATE);
//			Currency.updateCurrencyValue(INR.currencyCode, new CurrencySnapShot(20.0f, 0, LocalDateTime.now().toString()) );
			
			for(Currency c: Currency.CURRENCYSTATE.values())
			{
				Currency.updateCurrencyValue(c.currencyCode, new CurrencySnapShot(c.getValue().getvalueInINR() * 1.1f , 0, LocalDateTime.now().toString()) );
			}
			
//			Currency.updateCurrencyValue(currency1.currencyCode, new CurrencySnapShot(201.0f, 0, LocalDateTime.now().toString()) );
//			Currency.updateCurrencyValue(currency1.currencyCode, new CurrencySnapShot(202.0f, 0, LocalDateTime.now().toString()) );
//			Currency.updateCurrencyValue(currency1.currencyCode, new CurrencySnapShot(201.0f, 0, LocalDateTime.now().toString()) );
//			Currency.updateCurrencyValue(currency1.currencyCode, new CurrencySnapShot(150.0f, 0, LocalDateTime.now().toString()) );
//			Currency.updateCurrencyValue(currency1.currencyCode, new CurrencySnapShot(175.0f, 0, LocalDateTime.now().toString()) );
//			Currency.updateCurrencyValue(currency1.currencyCode, new CurrencySnapShot(190.0f, 0, LocalDateTime.now().toString()) );
//			Currency.updateCurrencyValue(currency1.currencyCode, new CurrencySnapShot(210.0f, 0, LocalDateTime.now().toString()) );
			
			System.out.println(Currency.CURRENCYSTATE.get("XRP"));
			
//			System.out.println(Currency.CURRENCYSTATE);
			System.out.println("Currency State");
//			for(Currency c: Currency.CURRENCYSTATE.values()){
//				System.out.println(c);
//			}
			//			sanchit.trade(t);
//			ObjectMapper m = new ObjectMapper();
//			System.out.println("\nClass toString");
//			System.out.println(sanchit);
//			String s = m.writeValueAsString(sanchit);
//			System.out.println("\nUser Class JSON");
//			System.out.println(s);
//			System.out.println("\n Currency Class JSON");
//			System.out.println(m.writeValueAsString(currency1));
			
//			for(int i=0;i<20;i++){
//				Thread.sleep(4000);
//				System.out.println("Sleeping for the " + i +"th time");
//			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){
//		MongoDatabase db = ServiceController.getDatabase();
		MongoClient mc = new MongoClient();
		MongoDatabase mdb = mc.getDatabase("CoinMonitor");
		MongoCollection<Document> collection = mdb.getCollection("Users");
		RefreshRate r = new RefreshRate();
		r.RefreshState();
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
		
		
		
//		MongoCollection<Document> collection = this.database.getCollection("HotTransactions");
//		System.out.println("Transaction initialized now inserting into DB " + this.getClass());
		
		
		
	}

}
