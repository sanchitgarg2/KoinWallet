package CoinMonitor.APIService;

import java.time.LocalDateTime;
import java.util.List;

import CoinMonitor.APIService.Currency.CurrencySnapShot;

public class WalletSection {
	Currency currency;
	float currentBalance;
	float cashInvested;
	float cashRedeemed;

	LocalDateTime purchaseDate;
	CurrencySnapShot purchasePrice;
	CurrencySnapShot sellPrice;
	LocalDateTime sellDate;
	public WalletSection(Currency currency, float numberOfCoins, LocalDateTime purchaseDate, CurrencySnapShot purchasePrice) {
		super();
		this.currency = currency;
		this.currentBalance = numberOfCoins;
		if(purchaseDate!= null)
		this.purchaseDate = purchaseDate;
		else this.purchaseDate = LocalDateTime.now();
		this.purchasePrice = purchasePrice;
	}
	
	public void buy(Transaction transaction){
		this.currentBalance += transaction.purchaseQuantity;
		this.cashInvested += transaction.purchaseQuantity*transaction.pricePerIncoming*transaction.outgoingCurrency.Value.valueInINR;
	};
	public void sell(Transaction transaction){
		this.cashRedeemed += transaction.purchaseQuantity*transaction.incomingCurrency.Value.valueInINR;
		this.currentBalance -= transaction.purchaseQuantity * transaction.pricePerIncoming;
	};
	
	public WalletSection() throws InvalidHoldingException {
		super();throw new InvalidHoldingException("Can not initialize an invalid Holding");
	}
	
	
}
