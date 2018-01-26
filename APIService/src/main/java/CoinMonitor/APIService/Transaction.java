package CoinMonitor.APIService;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Scope;

@Scope("prototype")
public class Transaction {
	Currency outgoingCurrency;
	Currency incomingCurrency;
	float pricePerIncoming;
	float purchaseQuantity;
	String time;
	
	
	
	
	
	public Transaction(Currency outgoingCurrency, Currency incomingCurrency, float pricePerIncoming,
			float purchaseQuantity) {
		super();
		this.outgoingCurrency = outgoingCurrency;
		this.incomingCurrency = incomingCurrency;
		this.pricePerIncoming = pricePerIncoming;
		this.purchaseQuantity = purchaseQuantity;
		this.time = LocalDateTime.now().toString();
	}
	public Transaction() {
	}
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
