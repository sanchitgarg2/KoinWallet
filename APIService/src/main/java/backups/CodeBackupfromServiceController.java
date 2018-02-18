package CoinMonitor.APIService;

import java.time.LocalDateTime;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import CoinMonitor.APIService.Currency.CurrencySnapShot;

public class CodeBackup {

/* From ServiceController. This is an Applicaiton init function which creates a few currencies and a test user.
 * private User getabc() throws Exception{
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
}*/
	
	
	/*	Pasted from Service Controller. I think these are replaced by the trade API
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
	
	*
	*
	*
		
	@RequestMapping(path="/SellCurrency",params = {"userID", "PIN"})
	public @ResponseBody String SellCurrency(HttpServletRequest Request, HttpServletResponse response,  @RequestParam(value = "userID") String userIDString,@RequestParam(value = "PIN") String PIN){
		int userID = Integer.parseInt(userIDString);
		try{			
		User user = getUser(userID);
		Transaction transaction = new Transaction();
		user.trade(transaction);
		updateUser(user);
		}
		catch(Exception e){
			return null;
		}
		return PIN;
	}
	
	*/
	
	
	/*	Pasted from the Service Controller class. I think this is replaced by the get Wallet API
	 * @RequestMapping(path="/getBalance",params = {"userID", "PIN"})
	public @ResponseBody String getBalance(HttpServletRequest Request, HttpServletResponse response,  @RequestParam(value = "userID") String userIDString,@RequestParam(value = "PIN") String PIN){
		int userID = Integer.parseInt(userIDString);
		try{
			User user = getUser(userID);
			return PIN;
		}
		catch(Exception e){
			return null;
		}
		
	}*/
	/* From Service Controller class. This was replaced by storing the Current State in a static variable in the app.
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
	 */
	

}
