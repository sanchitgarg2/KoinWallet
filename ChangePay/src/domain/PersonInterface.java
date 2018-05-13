package domain;

public interface PersonInterface {
	public String getPhoneNumber();
	public String getPersonType();
	public String getAccountBalance();
	public Object[] getLastNTransactions(int numberOfTransactions);
	public OTP generateOTP();
	public void sendOTP(OTP otp);
}
