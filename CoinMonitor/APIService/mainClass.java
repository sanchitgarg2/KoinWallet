package CoinMonitor.APIService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import CoinMonitor.APIService.Currency.CurrencySnapShot;

public class mainClass {

	public static void main(String[] args) {
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
			Currency INR = new Currency();
			INR.setCurrencyCode("INR");
			INR.setName("Rupee");
			INR.setValue(new CurrencySnapShot(1.0f, 0, LocalDateTime.now()));
			HashMap<LocalDateTime,CurrencySnapShot> INRHistory = new HashMap<LocalDateTime,CurrencySnapShot>();
			INRHistory.put(LocalDateTime.now(),new CurrencySnapShot(1.0f, 0, LocalDateTime.now()));
			INR.setHistory(INRHistory);
			Currency.makeNewCurrency(INR);			
			WalletSection section = new WalletSection(XRP,75.0f,LocalDateTime.now(),new CurrencySnapShot(200.0f, 0, LocalDateTime.now()));
			WalletSection sectionINR = new WalletSection(INR,75.0f,LocalDateTime.now(),new CurrencySnapShot(1.0f, 0, LocalDateTime.now()));
			wallet.addNewSection(section);
			wallet.addNewSection(sectionINR);
			sanchit.setWallet(wallet);
			
			t.setIncomingCurrency(Currency.CURRENCYSTATE.get("XRP"));
			
//			sanchit.trade(t);
			ObjectMapper m = new ObjectMapper();
			String s = m.writeValueAsString(sanchit);
			System.out.println(s);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
