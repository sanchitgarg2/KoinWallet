package dataAccess;

import java.io.IOException;
import java.util.ArrayList;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;

import domain.Constants;
import domain.Customer;
import domain.Key;
import domain.Merchant;
import domain.Person;
import domain.Session;
import exception.DataConsistencyException;
import exceptions.DataBaseException;
import exceptions.ExpiredOrMissingSessionException;
import exceptions.ObjectNotFoundException;
import exceptions.SystemInternalException;
import exceptions.UserNotFoundException;

public class SessionManagerDAO extends MongoAccessClass {
	static ObjectMapper mapper = new ObjectMapper();
	StaticMongoConfig config;

	public SessionManagerDAO() {
		config = new StaticMongoConfig();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public MongoCollection getCollection() {
		if (MongoAccessClass.getDatabase() != null) {
			return MongoAccessClass.getDatabase().getCollection(Constants.COLLECTIONS_SESSIONS);
		}
		return null;
	}

	public static Session getSession(ArrayList<Key> arrayList)
			throws SystemInternalException, ExpiredOrMissingSessionException {
		for (Key sessionKey : arrayList) {
			Document document;
			try {
				document = new SessionManagerDAO().getObjectByKeyValuePair("sessionKey.value", sessionKey.getValue());
				Session session = mapper.readValue(document.toJson(), Session.class);
				if (session.isValid()) {
					return session;
				} else {
					throw new ExpiredOrMissingSessionException("Session Expired.");
				}
			} catch (ObjectNotFoundException e) {
				throw new ExpiredOrMissingSessionException("Session Not Found");
			} catch (DataBaseException | IOException e) {
				throw new SystemInternalException(e);
			}
		}
		return null;
	}

	public static Session getSession(Key key) throws SystemInternalException, ExpiredOrMissingSessionException {
		ArrayList<Key> sessionKey = new ArrayList<>();
		sessionKey.add(key);
		try {
			return SessionManagerDAO.getSession(sessionKey);
		} catch (SystemInternalException | ExpiredOrMissingSessionException e) {
			throw e;
		}
	}

	public static Session getSession(String key) throws SystemInternalException, ExpiredOrMissingSessionException, DataBaseException {
		// TODO : Implement the session use counter here.
		Key sessionKey = new Key();
		sessionKey.setValue(key);
		ArrayList<Key> sessionKeyList = new ArrayList<>();
		sessionKeyList.add(sessionKey);
		try {

			try {
				Session session = SessionManagerDAO.getSession(sessionKeyList);
				session.getSessionKey().setLastAccessedTS(System.currentTimeMillis() + "");
				session.getSessionKey().useSession();
				(new SessionManagerDAO()).updateObjectWithKey("sessionKey.value", session.getSessionKey().getValue(), session);
				return session;
			} catch (JsonProcessingException | DataConsistencyException e) {
				// TODO Session being used could not be updated;
				e.printStackTrace();
			}
		} catch (SystemInternalException e) {
			throw e;
		}
		catch (ExpiredOrMissingSessionException e) {
			if(e.getMessage().contains("Session Expired."))
				SessionManagerDAO.deleteSession(key);
			throw e;
		}
		return null;
	}

	/*
	 * public static Session getSessions(ArrayList<String> stringKeys,
	 * ArrayList<Key> keys) throws SystemInternalException,
	 * ObjectNotFoundException{ //TODO : Implement the session use counter here.
	 * ArrayList<Key> sessionKeyList = new ArrayList<>(); if(stringKeys != null
	 * && stringKeys.size() > 0){ for(String s:stringKeys){ Key sessionKey = new
	 * Key(); sessionKey.setValue(s); sessionKeyList.add(sessionKey); } } else
	 * if (keys != null && keys.size() > 0 ){ sessionKeyList = keys; } else{
	 * return null; } try { return SessionManagerDAO.getSession(sessionKeyList);
	 * } catch (SystemInternalException e) { throw e; } }
	 */
	public static Person validateSession(Key sessionKey) throws UserNotFoundException, SystemInternalException, DataBaseException, ExpiredOrMissingSessionException, ObjectNotFoundException {
		try {
			Session session = SessionManagerDAO.getSession(sessionKey);
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
			// logger : log that there is a parsing error
			throw new SystemInternalException(e);
		}
	}

	public static Person validateSession(String sessionKey) throws ObjectNotFoundException, UserNotFoundException,
			SystemInternalException, DataBaseException, ExpiredOrMissingSessionException {
		try {
			Session session = SessionManagerDAO.getSession(sessionKey);
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
			// logger : log that there is a parsing error
			throw new SystemInternalException(e);
		}
	}

	public static ArrayList<Session> getLoginSessions(Person person)
			throws ObjectNotFoundException, UserNotFoundException, SystemInternalException,
			DataBaseException, ExpiredOrMissingSessionException {
		ArrayList<Session> validSessions = new ArrayList<>();
		for (Session s : person.getLoginSessions()) {
			Session validSession = (SessionManagerDAO.getSession(s.getId()));
			if (validSession != null) {
				validSessions.add(validSession);
			}
		}
		return validSessions;
	}

	public void createSession(Session session) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString;
		try {
			jsonInString = mapper.writeValueAsString(session);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			this.insertObject(Document.parse(jsonInString));
		} catch (JsonProcessingException e) {
			System.out.println(session);
			throw new Exception("Could not serialize the session JSON.");
		}
	}

	public static void deleteSession(String sessionKey) throws DataBaseException{
		(new SessionManagerDAO()).deleteObjectWithKey("sessionKey.value", sessionKey);
	}
}
