package CoinMonitor.APIService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import CoinMonitor.APIService.Currency.CurrencySnapShot;

import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
@Configuration
@EnableScheduling
public class mainClass {

//	@Autowired mainClass classA;
//	@Bean
	@Scheduled(fixedDelay = 10000)
	public void trial_method() {
		try {
			Transaction t = new Transaction();
			User sanchit = new User();
			sanchit.setEmailID("sanchitgarg2@gmail.com");
			sanchit.setLiquidCashInWallet(10f);
			sanchit.setPhoneNumber("8147325346");
			sanchit.setUSERID(10);
			CoinWallet wallet = new CoinWallet();
			Currency XRP = new Currency();
			XRP.setCurrencyCode("XRP");
			XRP.setName("Ripple");
			XRP.setValue(new CurrencySnapShot(200.0f, 0, LocalDateTime.now()));
			HashMap<LocalDateTime,CurrencySnapShot> XRPHistory = new HashMap<LocalDateTime,CurrencySnapShot>();
			XRPHistory.put(LocalDateTime.now(),new CurrencySnapShot(200.0f, 0, LocalDateTime.now()));
			XRP.setHistory(XRPHistory);
			Currency.makeNewCurrency(XRP);
			
			//SUB
			XRP = new Currency();
			XRP.setCurrencyCode("SUB");
			XRP.setName("Substratum");
			XRP.setValue(new CurrencySnapShot(40.0f, 0, LocalDateTime.now()));
			XRPHistory = new HashMap<LocalDateTime,CurrencySnapShot>();
			XRPHistory.put(LocalDateTime.now(),new CurrencySnapShot(38.0f, 0, LocalDateTime.now()));
			XRP.setHistory(XRPHistory);
			Currency.makeNewCurrency(XRP);
			
			//ETH
			
			XRP = new Currency();
			XRP.setCurrencyCode("ETH");
			XRP.setName("Etherium");
			XRP.setValue(new CurrencySnapShot(100000.0f, 0, LocalDateTime.now()));
			XRPHistory = new HashMap<LocalDateTime,CurrencySnapShot>();
			XRPHistory.put(LocalDateTime.now(),new CurrencySnapShot(92000.0f, 0, LocalDateTime.now()));
			XRP.setHistory(XRPHistory);
			Currency.makeNewCurrency(XRP);
			
			//TRX
			
			XRP = new Currency();
			XRP.setCurrencyCode("TRX");
			XRP.setName("Tron");
			XRP.setValue(new CurrencySnapShot(6.0f, 0, LocalDateTime.now()));
			XRPHistory = new HashMap<LocalDateTime,CurrencySnapShot>();
			XRPHistory.put(LocalDateTime.now(),new CurrencySnapShot(8.0f, 0, LocalDateTime.now()));
			XRP.setHistory(XRPHistory);
			Currency.makeNewCurrency(XRP);
			
			//XLM
			
			XRP = new Currency();
			XRP.setCurrencyCode("XLM");
			XRP.setName("Stellar Lumens");
			XRP.setValue(new CurrencySnapShot(15.0f, 0, LocalDateTime.now()));
			XRPHistory = new HashMap<LocalDateTime,CurrencySnapShot>();
			XRPHistory.put(LocalDateTime.now(),new CurrencySnapShot(26.0f, 0, LocalDateTime.now()));
			XRP.setHistory(XRPHistory);
			Currency.makeNewCurrency(XRP);
			
			//XVG
			
			XRP = new Currency();
			XRP.setCurrencyCode("XVG");
			XRP.setName("Verge");
			XRP.setValue(new CurrencySnapShot(26.0f, 0, LocalDateTime.now()));
			XRPHistory = new HashMap<LocalDateTime,CurrencySnapShot>();
			XRPHistory.put(LocalDateTime.now(),new CurrencySnapShot(31.0f, 0, LocalDateTime.now()));
			XRP.setHistory(XRPHistory);
			Currency.makeNewCurrency(XRP);
			
			Currency INR = new Currency();
			INR.setCurrencyCode("INR");
			INR.setName("Rupee");
			if(Currency.CURRENCYSTATE.containsKey("INR"))
				INR.setValue(new CurrencySnapShot(Currency.getCURRENCYSTATE().get("INR").getValue().valueInINR+1.0f, 0, LocalDateTime.now()));
			else
				INR.setValue(new CurrencySnapShot(1.0f, 0, LocalDateTime.now()));
			HashMap<LocalDateTime,CurrencySnapShot> INRHistory = new HashMap<LocalDateTime,CurrencySnapShot>();
			INRHistory.put(LocalDateTime.now(),new CurrencySnapShot(1.0f, 0, LocalDateTime.now()));
			INR.setHistory(INRHistory);
			Currency.makeNewCurrency(INR);			
//			WalletSection section = new WalletSection(XRP,75.0f,LocalDateTime.now(),new CurrencySnapShot(200.0f, 0, LocalDateTime.now()));
//			WalletSection sectionINR = new WalletSection(INR,75.0f,LocalDateTime.now(),new CurrencySnapShot(1.0f, 0, LocalDateTime.now()));
			WalletSection section;
			for(Currency c: Currency.CURRENCYSTATE.values())
			{
				 section = new WalletSection(c,c.getValue().valueInINR,LocalDateTime.now(),c.getValue());
				 wallet.addNewSection(section);
			}
			
//			wallet.addNewSection(section);
//			wallet.addNewSection(sectionINR);
			sanchit.setWallet(wallet);
			
			t.setIncomingCurrency(Currency.CURRENCYSTATE.get("XRP"));
//			System.out.println(Currency.CURRENCYSTATE);
			Currency.updateCurrencyValue(INR.currencyCode, new CurrencySnapShot(Currency.getCURRENCYSTATE().get("INR").getValue().valueInINR+20, 0, LocalDateTime.now()) );
			Currency.updateCurrencyValue(XRP.currencyCode, new CurrencySnapShot(201.0f, 0, LocalDateTime.now()) );
			Currency.updateCurrencyValue(XRP.currencyCode, new CurrencySnapShot(202.0f, 0, LocalDateTime.now()) );
			Currency.updateCurrencyValue(XRP.currencyCode, new CurrencySnapShot(201.0f, 0, LocalDateTime.now()) );
			Currency.updateCurrencyValue(XRP.currencyCode, new CurrencySnapShot(150.0f, 0, LocalDateTime.now()) );
			Currency.updateCurrencyValue(XRP.currencyCode, new CurrencySnapShot(175.0f, 0, LocalDateTime.now()) );
			Currency.updateCurrencyValue(XRP.currencyCode, new CurrencySnapShot(190.0f, 0, LocalDateTime.now()) );
			Currency.updateCurrencyValue(XRP.currencyCode, new CurrencySnapShot(210.0f, 0, LocalDateTime.now()) );
			
			System.out.println(Currency.CURRENCYSTATE.get("INR"));
			
//			System.out.println(Currency.CURRENCYSTATE);
			System.out.println("Currency State");
			for(Currency c: Currency.CURRENCYSTATE.values()){
				System.out.println(c);
			}
			//			sanchit.trade(t);
			ObjectMapper m = new ObjectMapper();
			System.out.println("\nClass toString");
			System.out.println(sanchit);
			String s = m.writeValueAsString(sanchit);
			System.out.println("\nUser Class JSON");
			System.out.println(s);
			System.out.println("\n Currency Class JSON");
			System.out.println(m.writeValueAsString(XRP));
			
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

}
