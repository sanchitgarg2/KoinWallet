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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((otp == null) ? 0 : otp.hashCode());
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
		OTP other = (OTP) obj;
		if (otp == null) {
			if (other.otp != null)
				return false;
		} else if (!otp.equals(other.otp))
			return false;
		return true;
	}
	
}
