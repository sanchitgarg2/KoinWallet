package domain;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.uuid.Generators;

import exceptions.AccessOverrideException;
import exceptions.InputMissingException;
import exceptions.InsufficientPaymentException;
import exceptions.MessageNotSentException;

public class Merchant implements PersonInterface{

	private String phoneNumber;
	Wallet wallet;
	String profileLastUpdatedTS;
	String personType;
	ArrayList<MerchantCategory> categories;
	String govtAuthNumber;
	String govtAuthType;
	String name;
	String address;
	String merchantID;
	ArrayList<String> currenciesAccepted;
	String latitude; 
	String longitude;
	Currency currency;
	String status;
	OTP loginOTP;
	SessionKey authorizationKey;
	SessionKey sessionKey;

	public SessionKey getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(SessionKey sessionKey) {
		this.sessionKey = sessionKey;
	}

	public Merchant() throws InputMissingException {
		super();
		this.merchantID = Generators.randomBasedGenerator().generate().toString();
		this.phoneNumber = "1234567890";
		this.govtAuthNumber = "TEST_AUTH_NUMBER";
		this.govtAuthType = "TEST_AUTH_TYPE";
		this.name = "JOHN DOE";
		this.address = "1402, Maple Heights";
	}

	public Merchant(String phoneNumber, String govtAuthNumber, String govtAuthType, String name, String address) {
		super();
		this.merchantID = Generators.randomBasedGenerator().generate().toString();
		this.phoneNumber = phoneNumber;
		this.govtAuthNumber = govtAuthNumber;
		this.govtAuthType = govtAuthType;
		this.name = name;
		this.address = address;
		this.setProfileLastUpdatedTS(""+System.currentTimeMillis());
		this.setSessionKey(new SessionKey());
		this.status = Constants.STATUS_LOGGED_OUT;
	}
	
	public String login() throws AccessOverrideException, MessageNotSentException{
		if(Constants.STATUS_LOGGED_IN.equals(this.getStatus()))
			throw new AccessOverrideException();
		if(Constants.STATUS_LOGGED_OUT.equals(this.getStatus())){
			this.loginOTP = this.generateOTP();
			this.loginOTP.setTransactionType(Constants.TRNASACTION_TYPE_AUTH);
			
			try {
				this.sendOTP(this.getLoginOTP());
				this.authorizationKey = new SessionKey();
			} catch (MessageNotSentException e) {
				this.loginOTP = null;
				throw new MessageNotSentException("Sending OTP failed.");
			}
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public OTP getLoginOTP() {
		return loginOTP;
	}

	public void setLoginOTP(OTP loginOTP) {
		this.loginOTP = loginOTP;
	}

	public SessionKey getAuthorizationKey() {
		return authorizationKey;
	}

	public void setAuthorizationKey(SessionKey authorizationKey) {
		this.authorizationKey = authorizationKey;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	//TODO:Initialize the Merchant Currency along with the Merchant
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	//TODO:initialize the currencies Accepted with default values being the custom currency, commonCash and commonCashBackwalaCash

	public ArrayList<String> getCurrenciesAccepted() {
		return currenciesAccepted;
	}

	public void setCurrenciesAccepted(ArrayList<String> currenciesAccepted) {
		this.currenciesAccepted = currenciesAccepted;
	}

	public String getMerchantID() {
		return merchantID;
	}

	public void setMerchantID(String merchantID) {
		this.merchantID = merchantID;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Wallet getWallet() {
		return wallet;
	}

	public String getGovtAuthNumber() {
		return govtAuthNumber;
	}

	public void setGovtAuthNumber(String govtAuthNumber) {
		this.govtAuthNumber = govtAuthNumber;
	}

	public String getGovtAuthType() {
		return govtAuthType;
	}

	public void setGovtAuthType(String govtAuthType) {
		this.govtAuthType = govtAuthType;
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

	public ArrayList<MerchantCategory> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<MerchantCategory> categories) {
		this.categories = categories;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setPersonType(String personType) {
		this.personType = personType;
	}

	public void addToCategory(MerchantCategory newCategory){
		if(this.categories == null)
			this.categories = new ArrayList<MerchantCategory>();
		categories.add(newCategory);
	}

	public void dropCategory(MerchantCategory categoryTobeDropped){
		if(this.categories == null)
			return; 
		this.categories.remove(categoryTobeDropped);
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
		return new OTP();
	}

	@Override
	public void sendOTP(OTP otp) throws MessageNotSentException {
		HashMap<String, String > smsParameters = new HashMap<>();
		smsParameters.put("phoneNumber", this.getPhoneNumber());
		smsParameters.put("otp", otp.getOTP());
		SendSMS.sendSarvMessage(smsParameters, 1);
	}

	public void claimOTP(String OTP, float amount) throws Exception{
		Transaction partialTransactionFromTheMerchant = new Transaction();
		OTP otp = new OTP();
		otp.setOTP(OTP);
		otp.setUseTS(System.currentTimeMillis() + "");
		otp.setAmount(0);
		otp.setExpiryTS(null);
		otp.setGenerationTS(null);
		otp.setLifeSpan(0);
		otp.setTransactionType(null);
		partialTransactionFromTheMerchant.setOTP(otp);
		partialTransactionFromTheMerchant.setMerchant(this);
		partialTransactionFromTheMerchant.setRequestedAmount(amount);
		partialTransactionFromTheMerchant.setStatus(null);
		partialTransactionFromTheMerchant.setTransactionRefNo(null);
		partialTransactionFromTheMerchant.setTransactionType(null);
		partialTransactionFromTheMerchant.setCustomerID(null);
		partialTransactionFromTheMerchant.processTransaction(partialTransactionFromTheMerchant);
	}
	public void collectPayment(Transaction transaction) throws InsufficientPaymentException{
		float amountCollected = 0;
		if(transaction.getPayment() == null || transaction.getPayment().size() == 0)
			throw new InsufficientPaymentException("Payment data is missing");
		ArrayList<Transaction> listofTransactions = new ArrayList<Transaction>();
		for (PaymentObject p : transaction.getPayment()){
			Transaction partialMerchantTransaction = new Transaction();
			ArrayList<PaymentObject> payments = new ArrayList<PaymentObject>();
			payments.add(p);
			partialMerchantTransaction.setPayment(payments);
			partialMerchantTransaction.setCurrencyCode(p.getCurrencyCode());
			partialMerchantTransaction.setRequestedAmount(p.amount);
			partialMerchantTransaction.setStatus(Constants.TRANSACTION_STATUS_COMPLETED);
			partialMerchantTransaction.setTransactionRefNo(transaction.getTransactionRefNo());
			partialMerchantTransaction.setTransactionType(Constants.TRNASACTION_TYPE_PARTIAL);
			listofTransactions.add(partialMerchantTransaction);
			amountCollected += p.getAmount();
			//TODO:getEquivalentAmount Later
		}
		if(transaction.getRequestedAmount() > amountCollected)
			throw new InsufficientPaymentException("The Payment fell short by " + (transaction.getRequestedAmount() - amountCollected ));
		for(Transaction t:listofTransactions){
			WalletSection walletSection;
			if(this.getWallet().getSections().containsKey(t.getPayment().get(0).getCurrencyCode())){
				walletSection = this.getWallet().getSections().get(t.getPayment().get(0).getCurrencyCode());
				walletSection.addBalance(t.getPayment().get(0).getAmount());
				walletSection.getTransactions().add(t);
			}
			else{
				//TODO:Create a new Section here. If possible, check if the merchant accepts the currency, nahi toh convert it.
				walletSection = new WalletSection(t.getPayment().get(0).getCurrencyCode());
				walletSection.addBalance(t.getPayment().get(0).getAmount());
				walletSection.getTransactions().add(t);				
			}

		}
	}
}