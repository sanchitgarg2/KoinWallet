package domain;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import dataAccess.CustomerDAO;
import dataAccess.MerchantDAO;
import dataAccess.SessionManagerDAO;
import exceptions.DataBaseException;
import exceptions.ObjectNotFoundException;
import exceptions.SystemInternalException;

public class Session {
	Key sessionKey;
	String userID;
	String userType;
	ArrayList<String> permissionsGranted;
	String source;
	private static ObjectMapper mapper = new ObjectMapper();

	Session(){
		super();
	}
	
	Session(String userID, String userType , String source){
		this.userID = userID;
		this.userType = userType;
		this.source = source;
		this.sessionKey = new Key();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	Session(Person person , String source) throws Exception{
		this.userID = person.getId();
		this.userType = person.getPersonType();
		this.source = source;
		this.sessionKey = new Key();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		(new SessionManagerDAO()).createSession(this);
	}

	public void endSession(){
		//		this.getPerson().logout();		
		//	TODO:end Session Code.

	}

	public static void createSession( Person person , String source) throws Exception{
		Session session = new Session(person.getId() , person.getPersonType() , source);
		(new SessionManagerDAO()).createSession(session);
	}

	public Key getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(Key sessionKey) {
		this.sessionKey = sessionKey;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public ArrayList<String> getPermissionsGranted() {
		return permissionsGranted;
	}

	public void setPermissionsGranted(ArrayList<String> permissionsGranted) {
		this.permissionsGranted = permissionsGranted;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@JsonIgnore
	public static Person getUser(Session session) throws DataBaseException, ObjectNotFoundException, SystemInternalException{
		try {
			switch(session.getUserType()){
			case Constants.PERSON_TYPE_CUSTOMER:{
				return mapper.readValue(new CustomerDAO().getObjectByKeyValuePair("customerID", session.getUserID()).toJson(),Customer.class);
			}
			case Constants.PERSON_TYPE_MERCHANT:{
				return mapper.readValue(new MerchantDAO().getObjectByKeyValuePair("merchantID", session.getUserID()).toJson(),Merchant.class);
			}
			default:{
				throw new SystemInternalException("Person Type is Invalid");
			}
			}
		} catch (IOException e) {
			throw new SystemInternalException(e);
		} 
	}


}
