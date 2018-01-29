package Coinclasses;

import java.util.ArrayList;
import java.util.List;

import Coinclasses.CoinWallet;
import Coinclasses.Currency;
import Coinclasses.Transaction;
import Coinclasses.WalletSection;

public class User {
	int USERID;
	CoinWallet wallet;
	List<Currency> watchList;
	String phoneNumber;
	String emailID;
	float LiquidCashInWallet;

	public List<Currency> getWatchList() {
		if(this.watchList == null)
		{
			ArrayList<Currency> tempList = new ArrayList<>();
			for(WalletSection w: this.wallet.sections.values()){
				Currency c = w.getCurrency();
				tempList.add(c);
			}
			this.watchList = tempList;
		}
		return watchList;
	}

	public void setWatchList(List<Currency> watchList) {
		this.watchList = watchList;
	}

	public int getUSERID() {
		return USERID;
	}

	public void setUSERID(int uSERID) {
		USERID = uSERID;
	}

	public CoinWallet getWallet() {
		return wallet;
	}

	public void setWallet(CoinWallet wallet) {
		this.wallet = wallet;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public float getLiquidCashInWallet() {
		return LiquidCashInWallet;
	}

	public void setLiquidCashInWallet(float liquidCashInWallet) {
		LiquidCashInWallet = liquidCashInWallet;
	}

	public User(){
		super();
	}

	public User(int uSERID, CoinWallet wallet, String phoneNumber, String emailID, float liquidCashInWallet) throws Exception {
		super();
		if (uSERID == 0)
			throw new Exception("Invalid USERID");
		USERID = uSERID;
		this.wallet = wallet;
		this.phoneNumber = phoneNumber;
		this.emailID = emailID;		LiquidCashInWallet = liquidCashInWallet;
	}

	public void trade(Transaction transaction) {
		this.wallet.trade(transaction);
	}

	@Override
	public String toString() {
		return "User [USERID=" + USERID + ", wallet=" + wallet + ", phoneNumber=" + phoneNumber + ", emailID=" + emailID
				+ ", LiquidCashInWallet=" + LiquidCashInWallet + "]";
	}



}
