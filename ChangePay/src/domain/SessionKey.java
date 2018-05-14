package domain;

import com.fasterxml.uuid.Generators;

public class SessionKey {
	String key;
	String generationTS;
	String expiryTS;
	int usageCount;
	int usage_limit;
	
	
	
	public SessionKey() {
		super();
		this.key = Generators.randomBasedGenerator().generate().toString();
		this.generationTS = "" + System.currentTimeMillis();
		this.expiryTS = ( Long.parseLong(this.generationTS) + Constants.SESSION_KEY_EXPIRY )+ ""; 
		this.usageCount = 0;
		this.usage_limit = Constants.SESSION_KEY_USAGE_LIMIT;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getGenerationTS() {
		return generationTS;
	}
	public void setGenerationTS(String generationTS) {
		this.generationTS = generationTS;
	}
	public String getExpiryTS() {
		return expiryTS;
	}
	public void setExpiryTS(String expiryTS) {
		this.expiryTS = expiryTS;
	}
	public int getUsageCount() {
		return usageCount;
	}
	public void setUsageCount(int usageCount) {
		this.usageCount = usageCount;
	}
	public int getUsage_limit() {
		return usage_limit;
	}
	public void setUsage_limit(int usage_limit) {
		this.usage_limit = usage_limit;
	} 
	
}
