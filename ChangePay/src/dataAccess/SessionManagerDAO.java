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
import domain.PaymentObject;
import domain.Person;
import domain.Session;
import domain.Wallet;
import domain.WalletSection;
import exceptions.CustomerAlreadyExistsException;
import exceptions.DataBaseException;
import exceptions.ElementAlreadyPresentException;
import exceptions.ObjectNotFoundException;
import exceptions.SessionNotFoundException;
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
	public MongoCollection getCollection(){
		if (MongoAccessClass.getDatabase()!= null){
			return MongoAccessClass.getDatabase().getCollection(Constants.COLLECTIONS_SESSIONS);
		}
		return null;
	}
	public static Session getSession(ArrayList<Key> arrayList) throws SystemInternalException, ObjectNotFoundException{
		for(Key sessionKey:arrayList){
			Document document;
			try {
				document = new SessionManagerDAO().getObjectByKeyValuePair("sessionKey.value", sessionKey.getValue());
				Session session = mapper.readValue(document.toJson() , Session.class);
				return session;
			} catch (ObjectNotFoundException  e) {
				throw e;
			}
			catch (DataBaseException | IOException e){
				throw new SystemInternalException(e);
			}
		}
		return null;
	}
	public static Session getSession(Key key) throws SystemInternalException,ObjectNotFoundException{
		ArrayList<Key> sessionKey = new ArrayList<>();
		sessionKey.add(key);
		try {
			return SessionManagerDAO.getSession(sessionKey);
		} catch (SystemInternalException | ObjectNotFoundException e) {
			throw e;
		}		
	}
	public static Session getSession(String key) throws SystemInternalException, ObjectNotFoundException{
		Key sessionKey = new Key();
		sessionKey.setValue(key);
		ArrayList<Key> sessionKeyList = new ArrayList<>();
		sessionKeyList.add(sessionKey);
		try {
			return SessionManagerDAO.getSession(sessionKeyList);
		} catch (SystemInternalException e) {
			throw e;
		}		
	}

	public static Person validateSession(Key sessionKey) throws ObjectNotFoundException,UserNotFoundException, SessionNotFoundException, SystemInternalException, DataBaseException{
		try {
			Session session = SessionManagerDAO.getSession(sessionKey);
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
			//logger : log that there is a parsing error
			throw new SystemInternalException(e);
		} 
	}
	public static Person validateSession(String sessionKey) throws ObjectNotFoundException,UserNotFoundException, SessionNotFoundException, SystemInternalException, DataBaseException{
		try {
			Session session = SessionManagerDAO.getSession(sessionKey);
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
			//logger : log that there is a parsing error
			throw new SystemInternalException(e);
		} 
	}
	public static Session validateSession(Person person) throws ObjectNotFoundException,UserNotFoundException, SessionNotFoundException, SystemInternalException, DataBaseException{
		return SessionManagerDAO.getSession(person.getSessionKeys());
	}

	public void createSession(Session session) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString;
		try {
			jsonInString = mapper.writeValueAsString(session);
			this.insertObject(Document.parse(jsonInString));
		} catch (JsonProcessingException e) {
			System.out.println(session);
			throw new Exception("Could not serialize the session JSON.");
		}
	}
}
