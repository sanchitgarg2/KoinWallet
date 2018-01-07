package CoinMonitor.APIService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CoinWallet {
	HashMap<String,WalletSection> sections = new HashMap<String,WalletSection>();
	List<Transaction> TransactionList;
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
			currentValue += h.currentBalance *((Currency)Currency.CURRENCYSTATE.get(h.currency.currencyCode)).value.getvalueInINR();
		}
		return currentValue;
	}

	public void trade(Transaction transaction) {
		if(this.TransactionList == null)
		{
			this.TransactionList = new ArrayList<Transaction>();
		}
		this.TransactionList.add(transaction);
		((WalletSection)this.sections.get(transaction.incomingCurrency.currencyCode)).buy(transaction);
		((WalletSection)this.sections.get(transaction.outgoingCurrency.currencyCode)).sell(transaction);
	}

	@Override
	public String toString() {
		return "CoinWallet [sections=" + sections + ", TransactionList=" + TransactionList + ", currentValue="
				+ currentValue + "]";
	}
	
	
}
