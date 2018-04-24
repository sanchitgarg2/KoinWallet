package CoinMonitor.APIService;

import java.time.LocalDateTime;
import org.apache.logging.log4j.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;

import com.fasterxml.jackson.annotation.JsonIgnore;

import CoinMonitor.APIService.CurrencySnapshot;
import CoinMonitor.APIService.Exceptions.CurrencyExchangeRateInvalidException;
import java.util.HashMap;

@Scope("prototype")
@SuppressWarnings("unchecked")
public class Currency{

	private String name;
	private String currencyCode;
	private CurrencySnapshot value;
	private HashMap<String, CurrencySnapshot> history; 
	private static HashMap<String,Currency> CURRENCYSTATE;
	private static Logger logger = LogManager.getLogger(Currency.class);


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



	@Override
	public String toString() {
		return "[name=" + name + " , currencyCode=" + currencyCode + ", value=" + value + "]\n";
	}


	@JsonIgnore
	public Currency getCurrencyState(){
		return CURRENCYSTATE.get(this.currencyCode);
	}


	@JsonIgnore
	public void updateHistory(CurrencySnapshot s){

		if(Currency.CURRENCYSTATE != null){
			Currency thisCurrency = Currency.CURRENCYSTATE.get(this.getCurrencyCode());
			if(thisCurrency.history != null){
				thisCurrency.history.put(LocalDateTime.now().toString(),s);
			}
			else{
				thisCurrency.history = new HashMap<String,CurrencySnapshot>();
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
		if(!Currency.getStaticCurrencyState().keySet().contains(newCurrency.currencyCode))
			Currency.getStaticCurrencyState().put(newCurrency.currencyCode, newCurrency);
		else{
			logger.log(Level.WARN,"Currency Already Exists");
		}
	}
	public static void deleteCurrency(Currency currency){
		Currency.getStaticCurrencyState().remove(currency.currencyCode);
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
	public CurrencySnapshot getValue() {
		if(Currency.getStaticCurrencyState() != null){
			if(!Currency.getStaticCurrencyState().keySet().contains(this.currencyCode)){
				Currency.setCURRENCYSTATE(new HashMap<String, Currency>());
				Currency.getStaticCurrencyState().put(this.currencyCode,this);
			}
			Currency thisCurrency = Currency.getCurrency(this.currencyCode);
			if(thisCurrency != null) {
				return thisCurrency.value;
			} else{
				logger.log(Level.FATAL,"Currency Value being asked for is not defined." , thisCurrency);
				return new CurrencySnapshot();
			}
		}
		else{
			logger.log(Level.FATAL,"Currency Value being asked for is not defined." , this);
			return (new CurrencySnapshot());
			//			throw new Exception("Stated Currency does not have a value : " + this.getCurrencyCode());
		}
	}

	@JsonIgnore
	public void setValue(CurrencySnapshot value) throws Exception {
		if(Currency.getStaticCurrencyState().containsKey(this.currencyCode)) {
			Currency thisCurrency = Currency.getStaticCurrencyState().get(this.currencyCode);
			thisCurrency.value = value;
			Currency.getStaticCurrencyState().put(this.currencyCode, thisCurrency);
		} else
		{
			this.value = value;
			Currency.getStaticCurrencyState().put(this.currencyCode, this);
		}
	}

	@JsonIgnore
	public HashMap<String,CurrencySnapshot> getHistory() {
		return history;
	}

	@JsonIgnore
	public void setHistory(HashMap<String,CurrencySnapshot> history) {
		Currency.getStaticCurrencyState().get(this.currencyCode).history = history;
	}

	@JsonIgnore
	public static void updateCurrencyValue(String currencyCode, CurrencySnapshot newValue) throws Exception
	{
		if(Currency.getStaticCurrencyState() == null)
			throw new Exception("Currency Map null, define a new currency first");
		else if (!Currency.getStaticCurrencyState().containsKey(currencyCode)){
			throw new Exception("Currency not found. Create the currency first");
		}
		else{
			Currency c = Currency.getStaticCurrencyState().get(currencyCode);
			c.setValue(newValue);
			c.history.put(LocalDateTime.now().toString(),newValue);
			Currency.getStaticCurrencyState().put(currencyCode,c);
		}
	}

	@JsonIgnore
	public static Currency getCurrency(String currencyCode){
		if(!Currency.getStaticCurrencyState().keySet().contains(currencyCode))
		{
			Currency newCurrency = new Currency();
			newCurrency.setCurrencyCode(currencyCode);
			Currency.makeNewCurrency(newCurrency);
		}
		return Currency.getStaticCurrencyState().get(currencyCode);

	}

	public static boolean doesCurrencyExist(String currencyCode){
		if (Currency.getStaticCurrencyState().keySet().contains(currencyCode)){
			return true;
		}
		else return false;
	}

	@JsonIgnore
	public static HashMap<String, Currency> getStaticCurrencyState() {
		if(Currency.CURRENCYSTATE == null)
			Currency.CURRENCYSTATE = new HashMap<String, Currency>();
		return CURRENCYSTATE;
	}

	@JsonIgnore
	public static void setCURRENCYSTATE(HashMap<String, Currency> cURRENCYSTATE) {
		CURRENCYSTATE = cURRENCYSTATE;
	}
}
