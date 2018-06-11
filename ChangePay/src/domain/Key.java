package domain;

import com.fasterxml.uuid.Generators;

public class Key {
	String value;
	String generationTS;
	String lastAccessedTS;
	String expiryTS;
	int maxInactiveInterval;
	int usageCount;
	int usage_limit;
	
	
	
	public Key() {
		super();
		this.value = Generators.randomBasedGenerator().generate().toString();
		this.generationTS = "" + System.currentTimeMillis();
		this.expiryTS = ( Long.parseLong(this.generationTS) + Constants.SESSION_KEY_EXPIRY )+ ""; 
		this.usageCount = 0;
		this.usage_limit = Constants.SESSION_KEY_USAGE_LIMIT;
		this.lastAccessedTS = null;
	}
	
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	public String getLastAccessedTS() {
		return lastAccessedTS;
	}
	public void setLastAccessedTS(String lastAccessedTS) {
		this.lastAccessedTS = lastAccessedTS;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String key) {
		this.value = key;
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
