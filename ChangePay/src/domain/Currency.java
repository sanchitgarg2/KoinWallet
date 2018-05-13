package domain;

import java.util.ArrayList;
import java.util.HashMap;

public class Currency {
	Merchant issuer;
	ArrayList<Transaction> transactions; 
	float currentOutstanding;
	float issuedTillDate;
	float settledTillDate; 
	float discountRate;
	HashMap<String,Float> discountRateHistory;
	String currencyCode;
	
	public float convertToOtherCurrency(String currencyCode){
		return 0;
		//TODO:insert logic to convert one currency to another over here.
	}
	public Merchant getIssuer() {
		return issuer;
	}
	public void setIssuer(Merchant issuer) {
		this.issuer = issuer;
	}
	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}
	public void setTransactions(ArrayList<Transaction> transactions) {
		this.transactions = transactions;
	}
	public float getCurrentOutstanding() {
		return currentOutstanding;
	}
	public void setCurrentOutstanding(float currentOutstanding) {
		this.currentOutstanding = currentOutstanding;
	}
	public float getIssuedTillDate() {
		return issuedTillDate;
	}
	public void setIssuedTillDate(float issuedTillDate) {
		this.issuedTillDate = issuedTillDate;
	}
	public float getSettledTillDate() {
		return settledTillDate;
	}
	public void setSettledTillDate(float settledTillDate) {
		this.settledTillDate = settledTillDate;
	}
	public float getDiscountRate() {
		return discountRate;
	}
	public void setDiscountRate(float discountRate) {
		this.discountRate = discountRate;
	}
	public HashMap<String, Float> getDiscountRateHistory() {
		return discountRateHistory;
	}
	public void setDiscountRateHistory(HashMap<String, Float> discountRateHistory) {
		this.discountRateHistory = discountRateHistory;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	public void updateDiscountRate(float newDiscountRate){
		if(this.discountRateHistory == null)
			this.discountRateHistory = new HashMap<String, Float>();
		String time = System.currentTimeMillis() + "";
		this.discountRateHistory.put(time, discountRate);
		//TODO:Implement this method.
	}
}
