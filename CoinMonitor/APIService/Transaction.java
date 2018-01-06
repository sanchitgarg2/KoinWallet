package CoinMonitor.APIService;

import java.time.LocalDateTime;

public class Transaction {
	Currency outgoingCurrency;
	Currency incomingCurrency;
	float pricePerIncoming;
	float purchaseQuantity;
	LocalDateTime time;
	
	public Currency getOutgoingCurrency() {
		return outgoingCurrency;
	}
	public void setOutgoingCurrency(Currency outgoingCurrency) {
		this.outgoingCurrency = outgoingCurrency;
	}
	public Currency getIncomingCurrency() {
		return incomingCurrency;
	}
	public void setIncomingCurrency(Currency incomingCurrency) {
		this.incomingCurrency = incomingCurrency;
	}
	public float getRate() {
		return pricePerIncoming;
	}
	public void setRate(float rate) {
		this.pricePerIncoming = rate;
	}
	public float getPurchaseQuantity() {
		return purchaseQuantity;
	}
	public void setPurchaseQuantity(float purchaseQuantity) {
		this.purchaseQuantity = purchaseQuantity;
	}
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
}
