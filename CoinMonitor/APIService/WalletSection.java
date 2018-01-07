package CoinMonitor.APIService;

import java.time.LocalDateTime;
import CoinMonitor.APIService.Currency.CurrencySnapShot;

public class WalletSection {
	Currency currency;
	float currentBalance;
	float cashInvested;
	float cashRedeemed;
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public float getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(float currentBalance) {
		this.currentBalance = currentBalance;
	}

	public float getCashInvested() {
		return cashInvested;
	}

	public void setCashInvested(float cashInvested) {
		this.cashInvested = cashInvested;
	}

	public float getCashRedeemed() {
		return cashRedeemed;
	}

	public void setCashRedeemed(float cashRedeemed) {
		this.cashRedeemed = cashRedeemed;
	}

	public WalletSection(Currency currency, float numberOfCoins, LocalDateTime purchaseDate, CurrencySnapShot purchasePrice) {
		super();
		this.currency = currency;
		this.currentBalance = numberOfCoins;
		this.cashInvested = numberOfCoins * purchasePrice.valueInINR;
	}
	
	public void buy(Transaction transaction){
		this.currentBalance += transaction.purchaseQuantity;
		this.cashInvested += transaction.purchaseQuantity*transaction.pricePerIncoming*transaction.outgoingCurrency.Value.valueInINR;
	};
	public void sell(Transaction transaction){
		this.cashRedeemed += transaction.purchaseQuantity*transaction.incomingCurrency.Value.valueInINR;
		this.currentBalance -= transaction.purchaseQuantity * transaction.pricePerIncoming;
	};
	
	public WalletSection(){
		super();
	}
	
	
}
