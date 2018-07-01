package domain;

import java.util.ArrayList;

import exceptions.AccessOverrideException;
import exceptions.MessageNotSentException;

public interface Person {
	public String getPhoneNumber();
	public String getPersonType();
	public String getId();
	public String getAccountBalance();
	public Object[] getLastNTransactions(int numberOfTransactions);
	public OTP generateOTP();
	public void sendOTP(OTP otp) throws MessageNotSentException;
	public String login() throws AccessOverrideException, MessageNotSentException;
	public Boolean login(String otp) throws Exception;
	public void logout();
	public ArrayList<Session> getLoginSessions();
}
