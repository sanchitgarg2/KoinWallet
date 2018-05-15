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
		if(sections != null) 
			return sections;
		else{
			this.setSections(new HashMap<String, WalletSection>());
			return sections;
		}
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

	public void processPayment(PaymentObject p,String tranactionID) {
		//Assume this money is coming into the wallet and then code.
		WalletSection walletSection;
		if(this.getSections().containsKey(p.getCurrencyCode())){
			walletSection = this.getSections().get(p.getCurrencyCode());
		}
		else{
			walletSection = new WalletSection(p.getCurrencyCode());
		}
		Transaction partialRecharge = new Transaction();
		partialRecharge.setTransactionRefNo(tranactionID);
		partialRecharge.setCurrencyCode(p.getCurrencyCode());
		partialRecharge.setCustomerID(this.getOwnerID());
		partialRecharge.setTransactionType(Constants.TRNASACTION_TYPE_PARTIAL);
		ArrayList<PaymentObject> payment  = new ArrayList<>();
		payment.add(p);
		partialRecharge.setPayment(payment);
		partialRecharge.setStatus(Constants.TRANSACTION_STATUS_COMPLETED);
		partialRecharge.setOTP(null);
		partialRecharge.setMerchantID(p.getSource());
		walletSection.addTransaction(partialRecharge);
		walletSection.setBalance(p.getAmount()+walletSection.getBalance());
		walletSection.setUpdateTS(System.currentTimeMillis()+"");
		this.setNetWorth(this.getNetWorth() + p.amount);
		this.getSections().put(p.getCurrencyCode(), walletSection);
	}
}
