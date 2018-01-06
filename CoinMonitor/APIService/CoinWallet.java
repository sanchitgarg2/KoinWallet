package CoinMonitor.APIService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import CoinMonitor.APIService.Currency.CurrencySnapShot;

public class CoinWallet {
	HashMap<String,WalletSection> sections = new HashMap<String,WalletSection>();
	List<Transaction> TransactionList;
	float currentValue;
	
	
	public float getCurrentValue() {
		this.currentValue = getCurrentValueinINR();
		return currentValue;
	}
//	public void setCurrentValue(float currentValue) {
//		this.currentValue = currentValue;
//	}
	
	private float getCurrentValueinINR(){
		float currentValue = 0;
		for(WalletSection h:this.sections.values()){
			currentValue += h.currentBalance *((Currency)Currency.CURRENCYSTATE.get(h.currency.currencyCode)).Value.getvalueInINR();
		}
		return currentValue;
	}

	public void trade(Transaction transaction) {
		//TODO:Add the logic here to 
		this.TransactionList.add(transaction);
		((WalletSection)this.sections.get(transaction.incomingCurrency.currencyCode)).buy(transaction);
		((WalletSection)this.sections.get(transaction.outgoingCurrency.currencyCode)).sell(transaction);
	}
}
