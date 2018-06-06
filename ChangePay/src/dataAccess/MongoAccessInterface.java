package dataAccess;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public interface MongoAccessInterface {
		
//	public MongoCollection getCollection();
//	public MongoDatabase getDatabase();
//	public static MongoClient getMongoClient() {
//		return null;
//	}
	public Object getObjectByObjectID(ObjectId objectID) throws Exception;
	public Object getObjectByKeyValuePair(String key, Object value) throws Exception;
	public void updateObjectWithKey(String key,String value, Object updatedObject) throws JsonProcessingException, Exception;
	public Boolean updateFieldOfObject(ObjectId objectIDKey, String fieldKey, Object fieldNewValue) throws Exception;
	public Boolean deleteFieldOfObject(ObjectId objectIDKey, String fieldKey) throws Exception;
	public Object getObjectsByKeyValuePair(String key, Object value) throws Exception;
	void deleteObjectWithKey(String key, Object value) throws Exception;
	Boolean insertObject(Document document);
}
