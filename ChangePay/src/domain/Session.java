package domain;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import dataAccess.CustomerDAO;
import dataAccess.MerchantDAO;
import dataAccess.SessionManagerDAO;
import exceptions.DataBaseException;
import exceptions.ObjectNotFoundException;
import exceptions.SystemInternalException;

public class Session implements HttpSession {
	Key sessionKey;
	String userID;
	String userType;
	ArrayList<String> permissionsGranted;
	String source;
	HashMap<String, Object> attributes;
	private static ObjectMapper mapper = new ObjectMapper();

	Session() {
		super();
	}

	public Session(String userID, String userType, String source) {
		this.userID = userID;
		this.userType = userType;
		this.source = source;
		this.sessionKey = new Key();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	Session(Person person, String source) throws Exception {
		this.userID = person.getId();
		this.userType = person.getPersonType();
		this.source = source;
		this.sessionKey = new Key();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		(new SessionManagerDAO()).createSession(this);
	}

	public HashMap<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, Object> attributes) {
		this.attributes = attributes;
	}

	public void endSession() {
		// this.getPerson().logout();
		// TODO:end Session Code.

	}

	public static void createSession(Person person, String source) throws Exception {
		Session session = new Session(person.getId(), person.getPersonType(), source);
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
	public static Person getUser(Session session)
			throws DataBaseException, ObjectNotFoundException, SystemInternalException {
		try {
			switch (session.getUserType()) {
			case Constants.PERSON_TYPE_CUSTOMER: {
				return mapper.readValue(
						new CustomerDAO().getObjectByKeyValuePair("customerID", session.getUserID()).toJson(),
						Customer.class);
			}
			case Constants.PERSON_TYPE_MERCHANT: {
				return mapper.readValue(
						new MerchantDAO().getObjectByKeyValuePair("merchantID", session.getUserID()).toJson(),
						Merchant.class);
			}
			default: {
				throw new SystemInternalException("Person Type is Invalid");
			}
			}
		} catch (IOException e) {
			throw new SystemInternalException(e);
		}
	}

	@Override
	public long getCreationTime() {
		return Long.parseLong(this.getSessionKey().getGenerationTS());
	}

	@Override
	public String getId() {
		return this.getSessionKey().getValue();
	}

	@Override
	public long getLastAccessedTime() {
		if (this.getSessionKey().getLastAccessedTS() != null)
			return Long.parseLong(this.getSessionKey().getLastAccessedTS());
		else
			return 0;
	}

	@Override
	@JsonIgnore
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		this.getSessionKey().setMaxInactiveInterval(interval);
	}

	@Override
	public int getMaxInactiveInterval() {
		return this.getSessionKey().getMaxInactiveInterval();
	}

	@Override
	@JsonIgnore
	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttribute(String name) {
		if (this.attributes != null && this.attributes.keySet().contains(name))
			return name;
		else
			return null;
	}

	@Override
	@JsonIgnore
	public Object getValue(String name) {
		if (this.attributes != null && this.attributes.keySet().contains(name))
			return this.attributes.get(name);
		else
			return null;
	}

	@Override
	@JsonIgnore
	public Enumeration getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getValueNames() {
		if (this.attributes != null)
			return (String[]) this.attributes.keySet().toArray();
		else
			return null;
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (this.attributes != null && this.attributes.keySet().contains(name))
			this.attributes.put(name, value);
		else {
			this.attributes = new HashMap<>();
			this.attributes.put(name, value);
		}
	}

	@Override
	public void putValue(String name, Object value) {
		if (this.attributes != null && this.attributes.keySet().contains(name))
			this.attributes.put(name, value);
		else {
			this.attributes = new HashMap<>();
			this.attributes.put(name, value);
		}
	}

	@Override
	public void removeAttribute(String name) {
		if (this.attributes != null && this.attributes.keySet().contains(name))
			this.attributes.remove(name);
	}

	@Override
	public void removeValue(String name) {
		if (this.attributes != null && this.attributes.keySet().contains(name))
			this.attributes.remove(name);
	}

	@Override
	public void invalidate() {
		this.endSession();

	}

	@Override
	public boolean isNew() {
		if (this.getSessionKey().getLastAccessedTS() == null)
			return true;
		return false;
	}

}
