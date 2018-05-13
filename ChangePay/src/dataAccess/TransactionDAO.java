package dataAccess;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;

import domain.Constants;
import domain.Merchant;
import domain.Transaction;
@SuppressWarnings("unchecked")
public class TransactionDAO extends MongoAccessClass{

	public void createNewTransaction(Transaction partialTransactionFromTheCustomer) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString;
		try {
			jsonInString = mapper.writeValueAsString(partialTransactionFromTheCustomer);
			this.getCollection().insertOne(Document.parse(jsonInString));
		} catch (JsonProcessingException e) {
			System.out.println(partialTransactionFromTheCustomer);
			throw new Exception("Could not serialize the Transaction JSON.");
		}
	}
	
	public void processTransaction(Merchant merchant, String OTP) throws Exception{
		this.getObjectByKeyValuePair("OTP.OTP", OTP);
	}

	public MongoCollection getCollection(){
		if (MongoAccessClass.getDatabase()!= null){
			return MongoAccessClass.getDatabase().getCollection(Constants.COLLECTIONS_TRANSACTIONS);
		}
		return null;
	}
}
