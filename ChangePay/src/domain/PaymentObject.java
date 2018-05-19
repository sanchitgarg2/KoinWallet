package domain;

import com.fasterxml.uuid.Generators;

public class PaymentObject {
	String currencyCode;
	float amount;
	String generationTimeStamp;
	String uuid;
	String source;
	String destination;
	
	
	
	
	public PaymentObject() {
		super();
		this.setUuid(Generators.randomBasedGenerator().generate().toString());
	}
	public float convertToOtherCurrency(String currencyCode){
		return amount;
		//TODO:write code to convert to other currency here.
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public String getGenerationTimeStamp() {
		return generationTimeStamp;
	}
	public void setGenerationTimeStamp(String generationTimeStamp) {
		this.generationTimeStamp = generationTimeStamp;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
}
