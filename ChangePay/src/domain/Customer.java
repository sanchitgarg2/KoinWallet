package domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import dataAccess.CustomerDAO;
import dataAccess.MerchantDAO;
import exceptions.AccessOverrideException;
import exceptions.InputMissingException;
import exceptions.InsufficientFundsException;

public class Customer implements PersonInterface{
	String customerID;
	private String phoneNumber;
	Wallet wallet = null;
	String profileLastUpdatedTS;
	String personType = null;
	ArrayList<CustomerCategory> categories = null;
	OTP loginOTP;
	String status;
	String lastLoginAt;
	SessionKey sessionKey; 
	SessionKey authorizationKey;
	@JsonIgnore
	CustomerDAO dao = new CustomerDAO();
	HashMap<String,Transaction> hotTransactions = new HashMap<String,Transaction>();

	public Customer() throws InputMissingException {
		super();
		this.setStatus(Constants.STATUS_LOGGED_OUT);
		this.setPhoneNumber("123456789");
		this.setCustomerID(null);
	}
	public Customer(String phoneNumber){
		super();
		this.setStatus(Constants.STATUS_LOGGED_OUT);
		this.setPhoneNumber(phoneNumber);
		this.setCustomerID(Generators.randomBasedGenerator().generate().toString());
	}
	public String login() throws AccessOverrideException{
		if(Constants.STATUS_LOGGED_IN.equals(this.getStatus()))
			throw new AccessOverrideException();
		if(Constants.STATUS_LOGGED_OUT.equals(this.getStatus())){
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
			if(		this.getLoginOTP().isMatching(OTP)&&
					this.getLoginOTP().isValid()){
				this.setLastLoginAt(System.currentTimeMillis()+"");
				this.sessionKey = new SessionKey();
				this.setStatus(Constants.STATUS_LOGGED_IN);
				return true;
			}
		}
		return false;
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

	public String startAuthTransaction() throws Exception{
		OTP otp = generateOTP();
		otp.setTransactionType(Constants.TRNASACTION_TYPE_AUTH);
		otp.setAmount(0);
		Transaction t = new Transaction();
		t.setOTP(otp);
		t.setStatus(Constants.TRANSACTION_STATUS_INITIATED);
		t.setTransactionType(Constants.TRNASACTION_TYPE_AUTH);
		t.setCustomerID(this.getCustomerID());
		this.getHotTransactions().put(otp.getOTP(), t);
		t.openTransaction(t);
		return otp.getOTP();
	}

	public String openTransaction(float amount) throws Exception{
		if(amount < this.getWallet().getNetWorth()){
			OTP otp = generateOTP();
			otp.setTransactionType(Constants.TRNASACTION_TYPE_CASH);
			otp.setAmount(amount);
			Transaction t = new Transaction();
			t.setOTP(otp);
			t.setStatus(Constants.TRANSACTION_STATUS_INITIATED);
//			t.setRequestedAmount(amount);
			//TODO:Remove invalid OTP and check the uniqueness of the OTP over here.
			t.setCustomerID(this.getCustomerID());
			this.getHotTransactions().put(otp.getOTP(), t);
			t.openTransaction(t);
			return otp.getOTP();
		}
		else{
			//TODO:Throw an error here that leads to the customer needing to add the rest of the money into his account.
			
			System.out.println("Please add money to the wallet.");
			throw new InsufficientFundsException();
		}
	}

	public Transaction settleTransaction(Transaction partialTransactionFromMerchant) throws JsonParseException, JsonMappingException, IOException, Exception{

		if(		this.getHotTransactions().keySet().contains(partialTransactionFromMerchant.getOTP().getOTP()) && 
				this.getHotTransactions().get(partialTransactionFromMerchant.getOTP().getOTP()).getOTP().isValid())
		{
			Transaction previousTransaction = this.getHotTransactions().get(partialTransactionFromMerchant.getOTP().getOTP());
			float amountDue = partialTransactionFromMerchant.getRequestedAmount();
//			if(amountDue != previousTransaction.getOTP().getAmount())
				//TODO:The amount does not match. Handle it. Throwing an Exception for now.
//				throw new InputMismatchException("The requested amount does not match");
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			
			ArrayList<String> currenciesAccepted = mapper.readValue((new MerchantDAO()).getObjectByKeyValuePair("merchantID",partialTransactionFromMerchant.getMerchantID()).toJson() , Merchant.class).getCurrenciesAccepted();
			ArrayList<PaymentObject> payment = new ArrayList<PaymentObject>();
			PaymentObject paymentObject = null;
			previousTransaction.getOTP().setUseTS(partialTransactionFromMerchant.getOTP().getUseTS());
			partialTransactionFromMerchant.setOTP(previousTransaction.getOTP());
			partialTransactionFromMerchant.setTransactionRefNo(previousTransaction.getTransactionRefNo());
			for(String currencyCode:currenciesAccepted)
			{
				if(amountDue == 0)
					break;
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
					paymentObject.setDestination(partialTransactionFromMerchant.getMerchantID());
					paymentObject.setAmount(applicableAmount);
					// TODO : Find the value of the currency used up.

					payment.add(paymentObject);
					amountDue -= paymentObject.getAmount();
				}					
					//reduce the due amount at the end of the transaction
			}
			if(amountDue == 0 && payment!= null && payment.size() > 0){
				for (PaymentObject t : payment){
					try {
						this.getWallet().getSections().get(t.getCurrencyCode()).dedductBalance(t.getAmount());
					} catch (InsufficientFundsException e) {
						// TODO One currency is short, check what to do with it.Check Roll back Logic
						e.printStackTrace();
					}
					Transaction partialTransaction = new Transaction();
					ArrayList<PaymentObject> temporaryPaymentObject = new ArrayList<PaymentObject>();
					temporaryPaymentObject.add(paymentObject);
					partialTransaction.setTransactionRefNo(partialTransactionFromMerchant.getTransactionRefNo());
					partialTransaction.setPayment(temporaryPaymentObject);
					partialTransaction.setCurrencyCode(t.getCurrencyCode());
					partialTransaction.setRequestedAmount(t.getAmount());
					partialTransaction.setStatus(Constants.TRANSACTION_STATUS_COMPLETED);
					partialTransaction.setTransactionType(Constants.TRNASACTION_TYPE_PARTIAL);
					this.getWallet().getSections().get(t.getCurrencyCode()).transactions.add(partialTransaction);
				}
			}
			else {
				throw new InsufficientFundsException();
			}
			//Create the Payment Objects
			//Update the customers wallet
			partialTransactionFromMerchant.setPayment(payment);
			partialTransactionFromMerchant.setCustomerID(previousTransaction.getCustomerID());
			partialTransactionFromMerchant.setStatus(Constants.TRANSACTION_STATUS_COMPLETED);
			this.getHotTransactions().remove(partialTransactionFromMerchant).getOTP().getOTP();
			//Populate the Payment Objects in the transaction
			//Update the Merchant Details
			//update the local copy of the transaction and remove it from hot transactions.
		}
		else{
			if(this.getHotTransactions().containsKey(partialTransactionFromMerchant.getOTP().getOTP())){
				this.getHotTransactions().remove(partialTransactionFromMerchant.getOTP().getOTP());
				//TODO: throw an exception here that the OTP expired?
			}
			
			partialTransactionFromMerchant = null;
		}
		return partialTransactionFromMerchant;
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
		return this.personType;
	}

	@Override
	public String getAccountBalance() {
		
		return null;
	}

	@Override
	public Object[] getLastNTransactions(int numberOfTransactions) {
		// TODO Auto-generated method - getLastNTransactions stub
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
	public void acceptPayment(ArrayList<PaymentObject> payment , String transactionID , String otp) {
		if(payment != null){
			if(this.getWallet() == null)
			{
				this.setWallet(new Wallet(this.getCustomerID()));
			}
		}
		WalletSection walletSection;
		for(PaymentObject paymentIterator : payment){
			this.getWallet().processPayment(paymentIterator , transactionID );
			this.getHotTransactions().remove(otp);
		}
	}
}
