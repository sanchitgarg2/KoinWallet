package CoinMonitor.APIService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.fasterxml.jackson.annotation.JsonIgnore;

import CoinMonitor.APIService.CurrencySnapshot;

public class Wallet {
	HashMap<String,WalletSection> sections = new HashMap<String,WalletSection>();
	List<Transaction> TransactionList;
	Logger logger = LogManager.getLogger(Currency.class);
	
	float currentValue;
	

	public HashMap<String, WalletSection> getSections() {
		return sections;
	}

	public void setSections(HashMap<String, WalletSection> sections) {
		this.sections = sections;
	}

	public List<Transaction> getTransactionList() {
		return TransactionList;
	}

	public void setTransactionList(List<Transaction> transactionList) {
		TransactionList = transactionList;
	}

	public void setCurrentValue(float currentValue) {
		this.currentValue = currentValue;
	}

	@JsonIgnore
	public float getCurrentValue() {
		this.currentValue = getCurrentValueinINR();
		return currentValue;
	}
	
	public void addNewSection(WalletSection section) throws Exception{
		//TODO: Add the value of this to the total wallet value.
		if(this.sections.containsKey(section.currency.getCurrencyCode())){
			throw new Exception("Section already Exists");
		}
		else{
			this.sections.put(section.currency.getCurrencyCode(), section);
		}
	}
	
	public void dropSection(String currencyCode) throws Exception{
		if(this.sections.containsKey(currencyCode)){
			this.sections.remove(currencyCode);
		}
		else{
			throw new Exception("Section Does not Exist");
		}
	}
	
	
	@JsonIgnore
	private float getCurrentValueinINR(){
		float currentValue = 0;
		for(WalletSection h:this.sections.values()){
			CurrencySnapshot currentPrice;
			try {
				currentPrice = h.currency.getValue();
				currentValue += h.currentBalance *(currentPrice.getValueInINR());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return currentValue;
	}

	public void trade(Transaction transaction) throws Exception {
		WalletSection incomingCurrencySection = this.sections.get(transaction.incomingCurrency.getCurrencyCode());
		WalletSection outgoingCurrencySection = this.sections.get(transaction.outgoingCurrency.getCurrencyCode());
		boolean buyTransactionSuccessful = false;
		boolean sellTransactionSuccessful = false;
		
		if(this.TransactionList == null)
		{
			this.TransactionList = new ArrayList<Transaction>();
		}
		try{
			if(incomingCurrencySection == null ){
				this.addNewSection(new WalletSection(transaction.incomingCurrency, transaction.purchaseQuantity, LocalDateTime.now(), new CurrencySnapshot(transaction.pricePerIncoming, 0f, System.currentTimeMillis()/1000, transaction.incomingCurrency.getCurrencyCode()))); 
				incomingCurrencySection = this.sections.get(transaction.incomingCurrency.getCurrencyCode());
			}
			if(outgoingCurrencySection == null ){
				this.addNewSection(new WalletSection(transaction.outgoingCurrency, -1 * (transaction.pricePerIncoming * transaction.purchaseQuantity), LocalDateTime.now(), new CurrencySnapshot((1/transaction.pricePerIncoming)*transaction.incomingCurrency.getValue().getValueInINR(), 0f, System.currentTimeMillis()/1000, transaction.incomingCurrency.getCurrencyCode())));
				outgoingCurrencySection = this.sections.get(transaction.outgoingCurrency.getCurrencyCode());
			}
		}
		catch (Exception e){
			logger.error("Error in creating new Wallet Section " + e.getMessage());
			throw e;
		}
		
		try {
			incomingCurrencySection.buy(transaction);
			buyTransactionSuccessful = true;
			outgoingCurrencySection.sell(transaction);
			sellTransactionSuccessful = true;
			this.TransactionList.add(transaction);
		}
		catch (Exception e) {
			logger.error(e.getMessage());
			if(!buyTransactionSuccessful)
				incomingCurrencySection.reverseTransaction(transaction);
			if(!sellTransactionSuccessful){
				outgoingCurrencySection.reverseTransaction(transaction);
			}
			throw e;
		}
	}

	@Override
	public String toString() {
		return "Wallet [sections=" + sections + ", TransactionList=" + TransactionList + ", currentValue="
				+ currentValue + "]";
	}
	
	
}
