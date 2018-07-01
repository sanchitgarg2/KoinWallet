package domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import domain.Constants;
import exceptions.MessageNotSentException;

public class SendSMS implements Runnable{
	public final static String USER_AGENT = "Mozilla/5.0";
	String messageUnencoded;
	String phoneNumber;
	HashMap<String, String> messageParameters;
	int Messagetype = -1;
	
	public SendSMS(String messageUnencoded, String phoneNumber) {
		super();
		this.messageUnencoded = messageUnencoded;
		this.phoneNumber = phoneNumber;
	}
	
	public SendSMS(HashMap<String, String> parameters, int Messagetype){
		super();
		this.messageParameters = parameters;
		this.Messagetype = Messagetype;
	} 
	
	
	@Override
	public void run() {
		try {
			if(this.Messagetype==-1)
			sendSMSinForeground(this.messageUnencoded, this.phoneNumber);
			else
			sendSarvMessage(this.messageParameters, this.Messagetype);
		} catch (IOException | MessageNotSentException e) {
			e.printStackTrace();
			System.out.println("Could not send message.");
		}
		
	}
	
	public static void sendSMSinBackGround(String messageUnencoded, String phoneNumber) throws IOException{
		Thread SMSThread = new Thread(new SendSMS(messageUnencoded, phoneNumber));
		SMSThread.start();
		}
	public static void sendOTPSMS(){
		if(!Constants.sendOTPMessage)
			return;
		
	}
	
	public static void sendSarvMessage(HashMap<String, String> parameters, int sarvMessageCode) throws MessageNotSentException{
		//&message=Your+OTP+is+313&mobile=8147325346
	
	String message="message=";
	String mobile="mobile=";
	try{
		if(parameters.get("phoneNumber") == null || "".equals(parameters.get("phoneNumber"))){
			throw new MessageNotSentException("PhoneNumber not present");
		}
		mobile += parameters.get("phoneNumber");
		switch(sarvMessageCode){
		case Constants.SARV_TEMPLATE_NUMBER_OTP:
			if(!Constants.sendOTPMessage){
				return;
			}
			if(parameters.get("otp") == null || "".equals(parameters.get("otp"))){
				throw new MessageNotSentException("OTP not present in OTP message");
			}
			message += Constants.SARV_TEMPLATE_OTP;
			message = message.replace("XXXX", parameters.get("otp"));
			break;
		case Constants.SARV_TEMPLATE_NUMBER_MERCH_TRANS_SUCC:
			if(!parameters.containsKey("amount")||!parameters.containsKey("accountBalance"))
				return;
			message += Constants.SARV_TEMPLATE_MERCH_TRANS_SUCC;
			message = message.replaceFirst("XXXX", parameters.get("amount"));
			message = message.replaceFirst("XXXX", parameters.get("accountBalance"));
			break;
		case Constants.SARV_TEMPLATE_NUMBER_MERCH_TRANS_FAILED:
			if(!parameters.containsKey("amount")||!parameters.containsKey("customer"))
				return;
			message += Constants.SARV_TEMPLATE_MERCH_TRANS_FAILED;
			message = message.replaceFirst("XXXX", parameters.get("amount"));
			message = message.replaceFirst("XXXX", parameters.get("customer"));
			break;
		case Constants.SARV_TEMPLATE_NUMBER_CUST_TRANS_SUCC:
			if(!Constants.sendAccBalMessage){
				return;
			}
			if(!parameters.containsKey("amount")||!parameters.containsKey("merchant")||!parameters.containsKey("accountBalance"))
				return;
			message += Constants.SARV_TEMPLATE_CUST_TRANS_SUCC;
			message = message.replaceFirst("XXXX", parameters.get("amount"));
			message = message.replaceFirst("XXXX", parameters.get("merchant"));
			message = message.replaceFirst("XXXX", parameters.get("accountBalance"));
			break;
		case Constants.SARV_TEMPLATE_NUMBER_CUST_TRANS_FAILED:
			if(!Constants.sendAccBalMessage){
				return;
			}
			if(!parameters.containsKey("amount")||!parameters.containsKey("merchant")||!parameters.containsKey("errormessage"))
				return;
			message += Constants.SARV_TEMPLATE_CUST_TRANS_FAILED;
			message = message.replaceFirst("XXXX", parameters.get("amount"));
			message = message.replaceFirst("XXXX", parameters.get("merchant"));
			message = message.replaceFirst("XXXX", parameters.get("errormessage"));
			break;
		case Constants.SARV_TEMPLATE_NUMBER_ACCOUNT_BALANCE:
			if(!Constants.sendAccBalMessage){
				return;
			}
			if(!parameters.containsKey("accountBalance"))
				return;
			message += Constants.SARV_TEMPLATE_ACCOUNT_BALANCE;
			message = message.replaceFirst("XXXX", parameters.get("accountBalance"));
			break;
		default:
			break;
		}
	String URL = Constants.SARV_BASE_URL + "&" + message + "&" + mobile;
	URL = URL.replace(" ", "+");
	URL obj = new URL(URL);
	HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	con.setRequestMethod("GET");
	con.setRequestProperty("User-Agent", USER_AGENT);
	int responseCode = con.getResponseCode();
	System.out.println("\nSending 'GET' request to URL : " + URL);
	System.out.println("Response Code : " + responseCode);
	} catch (IOException e) {
		throw new MessageNotSentException("No Internet Connection Available");
	}
	finally{
		
	}
	}
	public static void sendTransactionMessages(){}
	public static void sendSMSinForeground(String messageUnencoded, String phoneNumber) throws IOException{
		String url = "https://control.msg91.com/api/sendhttp.php?";
		String authKey = "authkey=133074ArSpCEmYY5846eb3e";
		String mobiles = "mobiles= " + "91" + phoneNumber;//
		String message = "message=" + messageUnencoded.replace(" ", "%20" );
		String sender = "sender=CHGPAY";
		String route= "route=4";
		String delim = "&";
		
		List<String> parameters = new ArrayList<>();
		parameters.add(authKey);
		parameters.add(mobiles);
		parameters.add(message);
		parameters.add(sender);
		parameters.add(route);
		
		String URLParams = String.join("&", parameters);
		URL obj = new URL(url + URLParams);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
		// optional default is GET
		con.setRequestMethod("GET");
	
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
	
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
	
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
	
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
	
		//print result
		System.out.println(response.toString());
	}

//	public SendSMS(String messageUnencoded, String phoneNumber) throws Exception {
//		super();
//		sendSMS(messageUnencoded, phoneNumber);
//	}

}
