package CoinMonitor.APIService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {
	
	public static HashMap<OTP, User> unverifiedUsers;
	public static HashMap<OTP, User> UsersLoggingIn;
	int USERID;
	CoinWallet wallet;
	List<Currency> watchList;
	String phoneNumber; 
	String emailID;
	String lastUsedDeviceID;
	String countryCode;
	
	
	@JsonIgnore
	public static HashMap<OTP, User> getUsersLoggingIn() {
		if(UsersLoggingIn == null)
			UsersLoggingIn = new HashMap<OTP, User>();
		return UsersLoggingIn;
	}

	@JsonIgnore
	public static void setUsersLoggingIn(HashMap<OTP, User> usersLoggingIn) {
		UsersLoggingIn = usersLoggingIn;
	}

	@JsonIgnore
	public static HashMap<OTP, User> getUnverifiedUsers() {
		if(unverifiedUsers == null)
		{
			unverifiedUsers = new HashMap<OTP, User>();
		}
		return unverifiedUsers;
	}

	@JsonIgnore
	public static void setUnverifiedUsers(HashMap<OTP, User> unverifiedUsers) throws Exception {
		if(unverifiedUsers == null)
		{
			throw new Exception("HashMap is uninitialized. getUnverifiedUsers() first");
		}
		
		User.unverifiedUsers = unverifiedUsers;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	float LiquidCashInWallet;
	
	public String getLastUsedDeviceID() {
		return lastUsedDeviceID;
	}

	public void setLastUsedDeviceID(String lastUsedDeviceID) {
		this.lastUsedDeviceID = lastUsedDeviceID;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + USERID;
		result = prime * result + ((emailID == null) ? 0 : emailID.hashCode());
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		int k = 0;
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (USERID != other.USERID)
			k=0;
		if (emailID == null) {
			if (other.emailID != null)
				return false;
		} else if (!emailID.equals(other.emailID))
			k=0;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		return true;
	}
	
	
	
}
