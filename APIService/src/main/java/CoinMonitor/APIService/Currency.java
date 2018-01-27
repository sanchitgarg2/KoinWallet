package CoinMonitor.APIService;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.logging.log4j.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.db.jpa.converter.LevelAttributeConverter;
import org.json.simple.JSONObject;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Scope;

import com.fasterxml.jackson.annotation.JsonIgnore;

import CoinMonitor.APIService.Currency.CurrencySnapShot;

import java.util.ArrayList;
import java.util.HashMap;

@Scope("prototype")
public class Currency{
	
	String name;
	String currencyCode;
	public CurrencySnapShot value;
	public HashMap<String, CurrencySnapShot> history; 
	public static HashMap<String,Currency> CURRENCYSTATE;
	public static Logger logger = LogManager.getLogger(Currency.class);
	
	
	//Define Supporting classes.
	class ConversionRate{
		Currency currency1;
		Currency currency2;
		private float buyCurrency2At;
		private float buyCurrency1At;
		public float getBuyCurrency2At() {
			return buyCurrency2At;
		}
		public void setBuyCurrency2At(float buyCurrency2At) throws CurrencyExchangeRateInvalidException {
			if(buyCurrency2At <= 0)
				throw new CurrencyExchangeRateInvalidException();
			this.buyCurrency2At = buyCurrency2At;
			this.buyCurrency1At = 1/buyCurrency2At;
		}
		public float getBuyCurrency1At() {
			return buyCurrency1At;
		}
		public void setBuyCurrency1At(float buyCurrency1At) throws CurrencyExchangeRateInvalidException {
			if(buyCurrency1At <= 0)
				throw new CurrencyExchangeRateInvalidException();
			this.buyCurrency1At = buyCurrency1At;
			this.buyCurrency2At = 1/buyCurrency1At;
		}
	}
	
	static class CurrencySnapShot{
		@Override
		public String toString() {
			return "valueInINR=" + valueInINR + ", valueInUSD=" + valueInUSD + ", refreshTime="
					+ refreshTime+" ";
		}
		public String toJSONString() {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("valueInINR", valueInINR);
			jsonObject.put("valueInUSD", valueInUSD);
			jsonObject.put("refreshTime",refreshTime);
			return jsonObject.toJSONString();
		}
		float valueInINR;
		float valueInUSD;
		String refreshTime;
		
		public CurrencySnapShot(float valueInINR, float valueInUSD, String refreshTime) {
			super();
			this.valueInINR = valueInINR;
			this.valueInUSD = valueInUSD;
			this.refreshTime = refreshTime;
		}


		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((refreshTime == null) ? 0 : refreshTime.hashCode());
			result = prime * result + Float.floatToIntBits(valueInINR);
			result = prime * result + Float.floatToIntBits(valueInUSD);
			return result;
		}


		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CurrencySnapShot other = (CurrencySnapShot) obj;
			if (refreshTime == null) {
				if (other.refreshTime != null)
					return false;
			} else if (!refreshTime.equals(other.refreshTime))
				return false;
			if (Float.floatToIntBits(valueInINR) != Float.floatToIntBits(other.valueInINR))
				return false;
			if (Float.floatToIntBits(valueInUSD) != Float.floatToIntBits(other.valueInUSD))
				return false;
			return true;
		}
		public float getvalueInINR() {
			if(this.valueInINR == 0)
				return this.valueInUSD * Currency.CURRENCYSTATE.get("USD").value.getvalueInINR();
			return this.valueInINR;
		}
	}
	
	
	
	@Override
	public String toString() {
		return " Currency [name=" + name + ", currencyCode=" + currencyCode + ", value=" + value + "]";
	}


	@JsonIgnore
	public Currency getCurrencyState(){
		return CURRENCYSTATE.get(this.currencyCode);
	}
	

	@JsonIgnore
	public void updateHistory(CurrencySnapShot s){
		
		if(Currency.CURRENCYSTATE != null){
			Currency thisCurrency = Currency.CURRENCYSTATE.get(this.getCurrencyCode());
			if(thisCurrency.history != null){
				thisCurrency.history.put(LocalDateTime.now().toString(),s);
				}
			else{
				thisCurrency.history = new HashMap<String,CurrencySnapShot>();
				thisCurrency.history.put(LocalDateTime.now().toString(),s);
			}
			Currency.CURRENCYSTATE.put(this.currencyCode, thisCurrency);
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((history == null) ? 0 : history.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Currency other = (Currency) obj;
		if (!currencyCode.equals(other.currencyCode))
				return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	public static void makeNewCurrency(Currency newCurrency){
		if(CURRENCYSTATE == null )
			CURRENCYSTATE = new HashMap<String,Currency>();
		if(!CURRENCYSTATE.keySet().contains(newCurrency.currencyCode))
			CURRENCYSTATE.put(newCurrency.currencyCode, newCurrency);
		else{
			logger.log(Level.WARN,"Currency Already Exists");
		}
	}
	public static void deleteCurrency(Currency currency){
		CURRENCYSTATE.remove(currency.currencyCode);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	@JsonIgnore
	public CurrencySnapShot getValue() throws Exception {
		if(Currency.CURRENCYSTATE != null){
			Currency thisCurrency = Currency.CURRENCYSTATE.get(this.currencyCode);
			if(thisCurrency != null) {
				return thisCurrency.value;
			} else{
				logger.log(Level.FATAL,"Currency Value being asked for is not defined." , thisCurrency);
				throw new Exception("Stated Currency does not have a value");}
		}
		else{
			Currency.CURRENCYSTATE = new HashMap<String, Currency>();
			Currency.CURRENCYSTATE.put(this.currencyCode,this);
			logger.log(Level.FATAL,"Currency Value being asked for is not defined." , this);
			throw new Exception("Stated Currency does not have a value");
		}
	}
	
	@JsonIgnore
	public void setValue(CurrencySnapShot value) throws Exception {
		if(Currency.CURRENCYSTATE != null){
			if(Currency.CURRENCYSTATE.containsKey(this.currencyCode)) {
				Currency thisCurrency = Currency.CURRENCYSTATE.get(this.currencyCode);
				thisCurrency.value = value;
				Currency.CURRENCYSTATE.put(this.currencyCode, thisCurrency);
			} else
			{
				Currency.CURRENCYSTATE.put(this.currencyCode, this);
				Currency thisCurrency = Currency.CURRENCYSTATE.get(this.currencyCode);
				thisCurrency.value = value;
				Currency.CURRENCYSTATE.put(this.currencyCode, thisCurrency);
			}
				
		}
		else{
			Currency.CURRENCYSTATE = new HashMap<String,Currency>();
			Currency.CURRENCYSTATE.put(this.currencyCode, this);
			Currency thisCurrency = Currency.CURRENCYSTATE.get(this.currencyCode);
			thisCurrency.value = value;
			Currency.CURRENCYSTATE.put(this.currencyCode, thisCurrency);
		}
	}
	
	@JsonIgnore
	public HashMap<String,CurrencySnapShot> getHistory() {
		return history;
	}
	
	@JsonIgnore
	public void setHistory(HashMap<String,CurrencySnapShot> history) {
		Currency.CURRENCYSTATE.get(this.currencyCode).history = history;
	}
	
	@JsonIgnore
	public static void updateCurrencyValue(String currencyCode, CurrencySnapShot newValue) throws Exception
	{
		if(CURRENCYSTATE == null)
			throw new Exception("Currency Map null, define a new currency first");
		else if (!CURRENCYSTATE.containsKey(currencyCode)){
			throw new Exception("Currency not found. Create the currency first");
		}
			else{
				Currency c = CURRENCYSTATE.get(currencyCode);
				c.setValue(newValue);
				c.history.put(LocalDateTime.now().toString(),newValue);
				Currency.CURRENCYSTATE.put(currencyCode,c);
			}
	}
	
	@JsonIgnore
	public static Currency getCurrency(String currencyCode){
		if(!Currency.CURRENCYSTATE.keySet().contains(currencyCode))
		{
			Currency newCurrency = new Currency();
			newCurrency.setCurrencyCode(currencyCode);
			Currency.makeNewCurrency(newCurrency);
		}
		return Currency.getCURRENCYSTATE().get(currencyCode);

	}
	
	
	@JsonIgnore
	public static HashMap<String, Currency> getCURRENCYSTATE() {
		if(Currency.CURRENCYSTATE == null)
			Currency.CURRENCYSTATE = new HashMap<String, Currency>();
		return CURRENCYSTATE;
	}
	
	@JsonIgnore
	public static void setCURRENCYSTATE(HashMap<String, Currency> cURRENCYSTATE) {
		CURRENCYSTATE = cURRENCYSTATE;
	}
}
