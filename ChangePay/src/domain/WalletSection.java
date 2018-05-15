package domain;

import java.util.ArrayList;

import exceptions.InputMissingException;
import exceptions.InsufficientFundsException;

public class WalletSection {
	String currencyCode;
	float balance;
	String updateTS;
	ArrayList<Transaction> transactions = null;
	public WalletSection() throws InputMissingException {
		super();
		throw new InputMissingException();
	}
	public WalletSection(String currencyCode) {
		super();
		this.currencyCode = currencyCode;
		this.updateTS = "" + System.currentTimeMillis();
		this.balance = 0;
	}	
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public float getBalance() {
		return balance;
	}
	public void setBalance(float balance) {
		this.balance = balance;
	}
	public void dedductBalance(float balance) throws InsufficientFundsException {
		balance = Math.abs(balance);
		if(balance > this.balance)
			throw new InsufficientFundsException("Currency " +this.getCurrencyCode() + "is less than what should be available. Requested is " + balance +" and avaialable is "+this.balance);
		this.balance -= balance;
	}
	public String getUpdateTS() {
		return updateTS;
	}
	public void setUpdateTS(String updateTS) {
		this.updateTS = updateTS;
	}
	public ArrayList<Transaction> getTransactions() {
		if(this.transactions == null)
			this.transactions = new ArrayList<Transaction>();
		return transactions;
	}
	public void setTransactions(ArrayList<Transaction> transactions) {
		this.transactions = transactions;
	}
	public void addBalance(float amount) {
		amount = Math.abs(amount);
		this.setBalance(this.getBalance() +amount);		
	}
	public void addTransaction(Transaction transaction) {
		if(this.getTransactions() == null)
			this.setTransactions(new ArrayList<Transaction>());
		this.getTransactions().add(transaction);
		
	}
	
}
