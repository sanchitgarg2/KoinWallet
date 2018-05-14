package dataAccess;

import java.util.ArrayList;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;

import domain.Constants;
import domain.Customer;
import domain.Merchant;
import domain.PaymentObject;
import domain.Wallet;
import domain.WalletSection;
import exceptions.CustomerAlreadyExistsException;
import exceptions.ElementAlreadyPresentException;

public class MerchantDAO extends MongoAccessClass {

	static StaticMongoConfig config;
	public MerchantDAO() {
		config = new StaticMongoConfig();
	}

	@Override
	public MongoCollection getCollection(){
		if (this.getDatabase()!= null){
			return this.getDatabase().getCollection(Constants.COLLECTIONS_MERCHANTS);
		}
		return null;
	}
	
	public void createMerchant (String phoneNumber, String govtAuthNumber, String govtAuthType, String name, String address ) throws CustomerAlreadyExistsException , Exception{
		//Check if phone Number Exists already.
		Merchant merchant = new Merchant(phoneNumber, govtAuthNumber, govtAuthType, name, address);
		//		merchant.setPhoneNumber(phoneNumber);
		//		customer.setPersonType(personType);
		Wallet wallet = new Wallet(merchant.getMerchantID());
		WalletSection walletSection;
		merchant.setWallet(wallet);
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString;
		try {
			jsonInString = mapper.writeValueAsString(merchant);
			this.getCollection().insertOne(Document.parse(jsonInString));
		} catch (JsonProcessingException e) {
			System.out.println(merchant);
			throw new Exception("Could not serialize the customer JSON.");
		}
	}
}
