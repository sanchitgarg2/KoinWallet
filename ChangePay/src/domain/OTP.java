package domain;

import java.util.Random;

public class OTP {
	public String OTP;
	public String generationTS;
	public String expiryTS;
	public String useTS;
	public int lifeSpan;
	public String transactionType;
	public float amount;
	
	public OTP() {
		super();
		String OTP = "";
		for(int i=0,N; i<Constants.OTP_Length ; i++){
			Random rand = new Random();
			N = rand.nextInt(9) + 1;
			OTP += N;
		}
		this.setOTP(OTP);
		this.setGenerationTS(System.currentTimeMillis()+"");
		this.setExpiryTS((System.currentTimeMillis() + Constants.OTP_LIFESPAN) + "");
		}
	
	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getUseTS() {
		return useTS;
	}
	public void setUseTS(String useTS) {
		this.useTS = useTS;
	}
	public String getOTP() {
		return OTP;
	}
	public void setOTP(String oTP) {
		OTP = oTP;
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
	public int getLifeSpan() {
		return lifeSpan;
	}
	public void setLifeSpan(int lifeSpan) {
		this.lifeSpan = lifeSpan;
	}
	public boolean isMatching(String OTP){
		if(OTP == null)
			return false;
		else
			return OTP.equals(this.getOTP());
	}
	public boolean isValid() {
		return System.currentTimeMillis() < Long.parseLong(this.getExpiryTS())  ? true:false;
	}
}
