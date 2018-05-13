package dataAccess;

import java.util.ArrayList;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;

import domain.Constants;
import domain.Customer;
import domain.PaymentObject;
import domain.Wallet;
import domain.WalletSection;
import exceptions.CustomerAlreadyExistsException;
import exceptions.ElementAlreadyPresentException;

public class CustomerDAO extends MongoAccessClass {

	static StaticMongoConfig config;
	public CustomerDAO() {
		config = new StaticMongoConfig();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public MongoCollection getCollection(){
		if (MongoAccessClass.getDatabase()!= null){
			return MongoAccessClass.getDatabase().getCollection(Constants.COLLECTIONS_CUSTOMER);
		}
		return null;
	}

	public void createCustomer(String phoneNumber, ArrayList<PaymentObject> initialCash) throws CustomerAlreadyExistsException , Exception{
		//Check if phone Number Exists already.
		//TODO : Perform phoneNumber validation here
		Customer customer = new Customer(phoneNumber);
		customer.setPhoneNumber(phoneNumber);
		//		customer.setPersonType(personType);
		customer.setProfileLastUpdatedTS(""+System.currentTimeMillis());
		Wallet wallet = new Wallet(customer.getCustomerID());
		WalletSection walletSection;
		for (PaymentObject p : initialCash)
		{
			walletSection = new WalletSection(p.getCurrencyCode());
			walletSection.setBalance(p.getAmount());
			try {
				wallet.addSection(walletSection);
			} catch (ElementAlreadyPresentException e) {
			}
		}
		customer.setWallet(wallet);
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString;
		try {
			jsonInString = mapper.writeValueAsString(customer);
			collection.insertOne(Document.parse(jsonInString));
		} catch (JsonProcessingException e) {
			System.out.println(customer);
			throw new Exception("Could not serialize the customer JSON.");
		}
	}

}
