package CoinMonitor.APIService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Currency{
	String name;
	String currencyCode;
	CurrencySnapShot Value;
	List<CurrencySnapShot> History; 
	public static HashMap<String,Currency> CURRENCYSTATE;
	
	public Currency getCurrencyState(){
		return CURRENCYSTATE.get(this.currencyCode);
	}
	
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
		float valueInINR;
		float valueInUSD;
		LocalDateTime refreshTime;
		
		public CurrencySnapShot(float valueInINR, float valueInUSD, LocalDateTime refreshTime) {
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
				return this.valueInUSD * Currency.CURRENCYSTATE.get("USD").Value.getvalueInINR();
			return this.valueInINR;
		}
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((History == null) ? 0 : History.hashCode());
		result = prime * result + ((Value == null) ? 0 : Value.hashCode());
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
		CURRENCYSTATE.put(newCurrency.currencyCode, newCurrency);
	}
	public static void deleteCurrency(Currency currency){
		CURRENCYSTATE.remove(currency.currencyCode);
	}
}
