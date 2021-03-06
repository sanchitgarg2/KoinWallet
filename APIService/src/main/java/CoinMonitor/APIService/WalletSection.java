package CoinMonitor.APIService;

import java.time.LocalDateTime;
import CoinMonitor.APIService.CurrencySnapshot;

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

	public WalletSection(Currency currency, float numberOfCoins, LocalDateTime purchaseDate, CurrencySnapshot purchasePrice) {
		super();
		this.currency = currency;
		this.currentBalance = numberOfCoins;
		this.cashInvested = numberOfCoins * purchasePrice.valueInINR;
	}
	
	public void buy(Transaction transaction) throws Exception{
		this.currentBalance += transaction.purchaseQuantity;
		this.cashInvested += transaction.purchaseQuantity*transaction.pricePerIncoming*transaction.outgoingCurrency.getValue().valueInINR;
	};
	public void sell(Transaction transaction) throws Exception{
		this.cashRedeemed += transaction.purchaseQuantity*transaction.incomingCurrency.getValue().valueInINR;
		this.currentBalance -= transaction.purchaseQuantity * transaction.pricePerIncoming;
	};
	
	public WalletSection(){
		super();
	}

	@Override
	public String toString() {
		return "[currency=" + currency + ", currentBalance=" + currentBalance + ", cashInvested=" + cashInvested + ", cashRedeemed=" + cashRedeemed + "]\n";
	}

	public void reverseTransaction(Transaction transaction) throws Exception {
		if(this.getCurrency() == transaction.getIncomingCurrency()){
			//reverse Buy Process
			this.currentBalance -= transaction.purchaseQuantity;
			this.cashInvested -= transaction.purchaseQuantity*transaction.pricePerIncoming*transaction.outgoingCurrency.getValue().valueInINR;
		} else if (this.getCurrency() == transaction.getOutgoingCurrency()){
			//reverse Sell Process
			this.cashRedeemed -= transaction.purchaseQuantity*transaction.incomingCurrency.getValue().valueInINR;
			this.currentBalance += transaction.purchaseQuantity * transaction.pricePerIncoming;
		
		}
		this.currentBalance += transaction.purchaseQuantity;
		this.cashInvested += transaction.purchaseQuantity*transaction.pricePerIncoming*transaction.outgoingCurrency.getValue().valueInINR;
	}
	
	
	
}
