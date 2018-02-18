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

import CoinMonitor.APIService.Currency.CurrencySnapShot;

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
		if(this.sections.containsKey(section.currency.currencyCode)){
			throw new Exception("Section already Exists");
		}
		else{
			this.sections.put(section.currency.currencyCode, section);
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
			currentValue += h.currentBalance *((Currency)Currency.CURRENCYSTATE.get(h.currency.currencyCode)).value.getEquivalentValueInINR();
		}
		return currentValue;
	}

	public void trade(Transaction transaction) {
		if(this.TransactionList == null)
		{
			this.TransactionList = new ArrayList<Transaction>();
		}
		this.TransactionList.add(transaction);
		if((WalletSection)this.sections.get(transaction.incomingCurrency.currencyCode) != null){
			((WalletSection)this.sections.get(transaction.incomingCurrency.currencyCode)).buy(transaction);
			if((WalletSection)this.sections.get(transaction.outgoingCurrency.currencyCode) != null)
				((WalletSection)this.sections.get(transaction.outgoingCurrency.currencyCode)).sell(transaction);
			else{
				//Outgoing Currency does not exist
				System.out.println("Currency being sold does not exist.");
				logger.warn("Currency being sold does not exist");
				}
			}
		else{
			try {
				this.addNewSection(new WalletSection(transaction.incomingCurrency, transaction.purchaseQuantity, LocalDateTime.now(), new CurrencySnapShot(transaction.pricePerIncoming, 0f, System.currentTimeMillis(), transaction.incomingCurrency.getCurrencyCode())));
			} catch (Exception e) {
				//An exception is thrown when you try to add a Currency that is already there. 
			}
		}
//		if((WalletSection)this.sections.get(transaction.outgoingCurrency.currencyCode) != null)
//			((WalletSection)this.sections.get(transaction.outgoingCurrency.currencyCode)).sell(transaction);
//		else{
//			//Outgoing Currency does not exist
//			System.out.println("Currency being sold does not exist.");
//			logger.warn("Currency being sold does not exist");
//			}
	}

	@Override
	public String toString() {
		return "Wallet [sections=" + sections + ", TransactionList=" + TransactionList + ", currentValue="
				+ currentValue + "]";
	}
	
	
}
