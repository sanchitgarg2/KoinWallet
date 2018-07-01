package CoinMonitor.APIService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;
public class CurrencySnapshot{
	
	String currencyCode;
	String currencyName;
	float valueInINR;
	float valueInUSD;
	long refreshTime;
	private static Logger logger = LogManager.getLogger(CurrencySnapshot.class);
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public void setValueInINR(Float valueInINR) {
		this.valueInINR = valueInINR;
	}
	public float getValueInUSD() {
		return valueInUSD;
	}
	public void setValueInUSD(Float valueInUSD) {
		this.valueInUSD = valueInUSD;
	}
	public long getRefreshTime() {
		return refreshTime ;
	}
	public void setRefreshTime(Long refreshTime) {
		this.refreshTime = refreshTime;
	}
//	public void setRefreshTime(int refreshTime) {
//		this.refreshTime = refreshTime;
//	}
	public void setValueInINR(Double valueInINR){
		this.valueInINR = Float.parseFloat(valueInINR.toString());
	}
	
	@Override
	public String toString() {
		return currencyCode.toUpperCase() + " valueInINR=" + valueInINR + ", valueInUSD=" + valueInUSD + ", refreshTime="
				+ refreshTime+" ";
	}
	
	@JsonIgnore
	public String getSimplifiedJSONString(){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("valueInINR", valueInINR);
		jsonObject.put("refreshTime",this.getRefreshTime());
		jsonObject.put("currencyCode", currencyCode);
		return jsonObject.toJSONString();
	}
	
	@JsonIgnore
	public String getJSONString() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("valueInINR", valueInINR);
		jsonObject.put("valueInUSD", valueInUSD);
		jsonObject.put("refreshTime",this.getRefreshTime());
		jsonObject.put("currencyCode", currencyCode);
		jsonObject.put("currencyName", currencyName);
		return jsonObject.toJSONString();
	}
	public CurrencySnapshot(float valueInINR, float valueInUSD, long refreshTime,String currencyCode) {
		super();
		this.valueInINR = valueInINR;
		this.valueInUSD = valueInUSD;
		this.refreshTime = refreshTime;
		this.currencyCode = currencyCode;
		try{
		this.currencyName = Currency.getStaticCurrencyState().get(currencyCode).getName();}
		catch(Exception e){
			logger.warn("Currency not in Currency State",valueInINR, valueInUSD,refreshTime,currencyCode);				
		}
	}
	
	public  CurrencySnapshot(){
		super(); 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
		result = prime * result + (int) (refreshTime ^ (refreshTime >>> 32));
		result = prime * result + Float.floatToIntBits(valueInINR);
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
		CurrencySnapshot other = (CurrencySnapshot) obj;
		if (currencyCode == null) {
			if (other.currencyCode != null)
				return false;
		} else if (!currencyCode.equals(other.currencyCode))
			return false;
		if (refreshTime != other.refreshTime)
			return false;
		if (Float.floatToIntBits(valueInINR) != Float.floatToIntBits(other.valueInINR))
			return false;
		return true;
	}
	
	@JsonIgnore
	public float getValueInINR() {
		if(this.valueInINR == 0 && this.valueInUSD != 0){
			if(Currency.doesCurrencyExist("USD"))
				this.valueInINR = this.valueInUSD * Currency.getCurrency("USD").getValue().getValueInINR();
			else
				this.valueInINR = this.valueInUSD * 64.025f;
		}
		return this.valueInINR;
	}
	
	public void setCode(String code){
		this.setCurrencyCode(code);
	}
	public void setOdate(long date){
		this.setRefreshTime(date);
	}
	public void setValue(float value){
		this.setValueInINR(value);
	}
}

