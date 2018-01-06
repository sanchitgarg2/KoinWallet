package CoinMonitor.APIService;

import java.util.List;

public class User {
	int USERID;
	CoinWallet wallet;
	String phoneNumber; 
	String emailID;
	List<WalletSection> previousHoldings;
	
	float LiquidCashInWallet;
	
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
	
}
