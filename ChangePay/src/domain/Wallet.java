package domain;

import java.util.ArrayList;
import java.util.HashMap;

import exceptions.ElementAlreadyPresentException;
import exceptions.InputMissingException;

public class Wallet {
	String ownerID;
	HashMap<String,WalletSection> sections = null;
	float netWorth;


	public Wallet(){
		super();
		this.ownerID = "NULL";
	}

	public Wallet(String ownerID) {
		super();
		this.ownerID = ownerID;
	}
	public String getOwnerID() {
		return ownerID;
	}
	public void setOwnerID(String customerID) {
		this.ownerID = customerID;
	}
	public HashMap<String,WalletSection> getSections() {
		return sections;
	}
	public void setSections(HashMap<String,WalletSection> sections) {
		this.sections = sections;
	}
	public float getNetWorth() {
		return netWorth;
	}
	public void setNetWorth(float netWorth) {
		this.netWorth = netWorth;
	}

	public void addSection(WalletSection walletSection) throws ElementAlreadyPresentException {
		if (this.getSections() == null)
			this.sections = new HashMap<String,WalletSection>();
		if(this.getSections().keySet().contains(walletSection.getCurrencyCode())){
			throw new ElementAlreadyPresentException();
		}
		//		this.setNetWorth(this.getNetWorth() + walletSection.ge
		//TODO: Add the networth of every section to that of the wallet
		this.getSections().put(walletSection.getCurrencyCode(), walletSection);

	}
	public void dropSection(WalletSection walletSection){
		this.dropSection(walletSection.getCurrencyCode());
	}
	public void dropSection(String currencyCode){
		this.getSections().remove(currencyCode);
	}

}
