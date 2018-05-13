package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;

import com.fasterxml.uuid.Generators;
import dataAccess.CustomerDAO;
import exceptions.AccessOverrideException;
import exceptions.InputMissingException;
import exceptions.InsufficientFundsException;

public class Customer implements PersonInterface{
	String customerID;
	private String phoneNumber;
	Wallet wallet = null;
	String profileLastUpdatedTS;
	String personType;
	ArrayList<CustomerCategory> categories = null;
	OTP loginOTP;
	String status;
	String lastLoginAt;
	SessionKey sessionKey; 
	SessionKey authorizationKey;
	CustomerDAO dao = new CustomerDAO();
	HashMap<String,Transaction> hotTransactions = new HashMap<String,Transaction>();

	public Customer() throws InputMissingException {
		super();
		throw new InputMissingException();
	}
	public Customer(String phoneNumber){
		super();
		this.setStatus(Constants.STATUS_LOGGED_OUT);
		this.setPhoneNumber(phoneNumber);
		this.setCustomerID(Generators.randomBasedGenerator().toString());
	}
	public String login() throws AccessOverrideException{
		if(this.getStatus() == Constants.STATUS_LOGGED_IN)
			throw new AccessOverrideException();
		if(this.getStatus() == Constants.STATUS_LOGGED_OUT){
			this.loginOTP = this.generateOTP();
			this.loginOTP.setTransactionType(Constants.TRNASACTION_TYPE_AUTH);
			this.authorizationKey = new SessionKey();
			this.sendOTP(this.getLoginOTP());
			return this.getLoginOTP().getOTP();
		}
		return null;
	}
	public Boolean login(String OTP){
		if(this.getLoginOTP() != null){
			if(this.getLoginOTP().isMatching(OTP)&&this.getLoginOTP().isValid()){
				return true;
			}
		}
		return false;
	}
	public CustomerDAO getDao() {
		return dao;
	}
	public void setDao(CustomerDAO dao) {
		this.dao = dao;
	}
	public OTP getLoginOTP() {
		return loginOTP;
	}
	public void setLoginOTP(OTP loginOTP) {
		this.loginOTP = loginOTP;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLastLoginAt() {
		return lastLoginAt;
	}
	public void setLastLoginAt(String lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}
	public SessionKey getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(SessionKey sessionKey) {
		this.sessionKey = sessionKey;
	}
	public SessionKey getAuthorizationKey() {
		return authorizationKey;
	}
	public void setAuthorizationKey(SessionKey authorizationKey) {
		this.authorizationKey = authorizationKey;
	}
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}


	public String openTransaction(float amount) throws Exception{
		if(amount < this.getWallet().getNetWorth()){
			OTP otp = generateOTP();
			otp.setTransactionType(Constants.TRNASACTION_TYPE_CASH);
			otp.setAmount(amount);
			Transaction t = new Transaction();
			t.setOTP(otp);
			t.setStatus(Constants.TRANSACTION_STATUS_INITIATED);
			t.setRequestedAmount(amount);
			this.getHotTransactions().put(otp.getOTP(), t);
			t.openTransaction(t);
			return otp.getOTP();
		}
		else{
			//TODO:Throw an error here that leads to the customer needing to add the rest of the money into his account.
			System.out.println("Please add money to the wallet.");
			return null;
		}
	}
	
	public Transaction settleTransaction(Transaction transaction){
		
		if(		this.getHotTransactions().keySet().contains(transaction.getOTP().getOTP()) && 
				this.getHotTransactions().get(transaction.getOTP().getOTP()).getOTP().isValid())
		{
			Transaction previousTransaction = this.getHotTransactions().get(transaction.getOTP().getOTP());
			float amountDue = transaction.getRequestedAmount();
			if(amountDue != previousTransaction.getRequestedAmount())
				//TODO:The amount does not match. Handle it. Throwing an Exception for now.
				throw new InputMismatchException("The requested amount does not match");
			ArrayList<String> currenciesAccepted = transaction.getMerchant().getCurrenciesAccepted();
			ArrayList<PaymentObject> payment = new ArrayList<PaymentObject>();
			PaymentObject paymentObject = null;
			for(String currencyCode:currenciesAccepted)
			{
				if(this.getWallet().getSections().containsKey(currencyCode))
				{
					float availableAmount = this.getWallet().getSections().get(currencyCode).getBalance();
					//TODO:Insert logic to apply only a limited amount here.
					float applicableAmount = availableAmount;
					if(availableAmount >= amountDue){
						applicableAmount = amountDue;
					}
					paymentObject = new PaymentObject();
					paymentObject.setCurrencyCode(currencyCode);
					paymentObject.setSource(this.getCustomerID());
					paymentObject.setDestination(transaction.getMerchant().getMerchantID());
					paymentObject.setAmount(applicableAmount);
					Transaction partialTransaction = new Transaction();
					ArrayList<PaymentObject> temporaryPaymentObject = new ArrayList<PaymentObject>();
					temporaryPaymentObject.add(paymentObject);
					partialTransaction.setTransactionRefNo(transaction.getTransactionRefNo());
					partialTransaction.setPayment(temporaryPaymentObject);
					partialTransaction.setCurrencyCode(currencyCode);
					partialTransaction.setRequestedAmount(applicableAmount);
					partialTransaction.setStatus(Constants.TRANSACTION_STATUS_COMPLETED);
					partialTransaction.setTransactionType(Constants.TRNASACTION_TYPE_PARTIAL);			
					// TODO : Find the value of the currency used up.
					try {
						this.getWallet().getSections().get(currencyCode).dedductBalance(applicableAmount);
						this.getWallet().getSections().get(currencyCode).transactions.add(partialTransaction);
						payment.add(paymentObject);
						amountDue -= paymentObject.getAmount();
					} catch (InsufficientFundsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//reduce the due amount at the end of the transaction
				}
			}
			//Create the Payment Objects
			//Update the customers wallet
			transaction.setPayment(payment);
			transaction.setStatus(Constants.TRANSACTION_STATUS_COMPLETED);
			this.getHotTransactions().remove(transaction).getOTP().getOTP();
			//Populate the Payment Objects in the transaction
			//Update the Merchant Details
			//update the local copy of the transaction and remove it from hot transactions.
		}
		else{
			if(this.getHotTransactions().containsKey(transaction.getOTP().getOTP()))
				this.getHotTransactions().remove(transaction.getOTP().getOTP());
			transaction = null;
		}
		return transaction;
	}
	
	public HashMap<String, Transaction> getHotTransactions() {
		return hotTransactions;
	}
	public void setHotTransactions(HashMap<String, Transaction> hotTransactions) {
		this.hotTransactions = hotTransactions;
	}
	public void addToCategory(CustomerCategory newCategory){
		if(this.categories == null)
			this.categories = new ArrayList<CustomerCategory>();
		categories.add(newCategory);
	}

	public void dropCategory(CustomerCategory categoryTobeDropped){
		if(this.categories == null)
			return; 
		this.categories.remove(categoryTobeDropped);
	}
	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

	public String getProfileLastUpdatedTS() {
		return profileLastUpdatedTS;
	}

	public void setProfileLastUpdatedTS(String profileLastUpdatedTS) {
		this.profileLastUpdatedTS = profileLastUpdatedTS;
	}

	public ArrayList<CustomerCategory> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<CustomerCategory> categories) {
		this.categories = categories;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setPersonType(String personType) {
		this.personType = personType;
	}

	public Object applyCategoryFilters(){

		return null;
	}

	@Override
	public String getPhoneNumber() {
		return this.phoneNumber;
	}
	@Override
	public String getPersonType() {
		return this.getPersonType();
	}

	@Override
	public String getAccountBalance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getLastNTransactions(int numberOfTransactions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OTP generateOTP() {
		OTP otp = new OTP();
		return otp;
	}
	@Override
	public void sendOTP(OTP otp) {
		//TODO:Write Code here to Send the OTP message.
	}

}
