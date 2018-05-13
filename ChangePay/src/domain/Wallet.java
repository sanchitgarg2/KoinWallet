package domain;

import java.util.ArrayList;
import java.util.HashMap;

import exceptions.ElementAlreadyPresentException;
import exceptions.InputMissingException;

public class Wallet {
	String customerID;
	HashMap<String,WalletSection> sections = null;
	float netWorth;


	public Wallet() throws InputMissingException {
		super();
		throw new InputMissingException();
	}

	public Wallet(String customerID) {
		super();
		this.customerID = customerID;
	}
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
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
