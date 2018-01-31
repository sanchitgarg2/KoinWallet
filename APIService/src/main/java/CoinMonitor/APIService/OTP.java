package CoinMonitor.APIService;

import java.time.LocalDateTime;

public class OTP {
	String otp;
	LocalDateTime generationDate;
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public LocalDateTime getGenerationDate() {
		return generationDate;
	}
	public void setGenerationDate(LocalDateTime generationDate) {
		this.generationDate = generationDate;
	}
	
}
