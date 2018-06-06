package dataAccess;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import domain.Constants;
import exceptions.DataBaseException;
import exceptions.ObjectNotFoundException;

public class MongoAccessClass implements MongoAccessInterface{
	static MongoClient mongoClient;
	MongoCollection<Document> collection;
	MongoDatabase database;

	@SuppressWarnings("rawtypes")
	public MongoCollection<Document> getCollection() {
		return this.collection;
	}

	public static MongoDatabase getDatabase() {
		if(MongoAccessClass.getMongoClient() != null)
			return MongoAccessClass.getMongoClient().getDatabase(Constants.DATABASE_CHANGEPAY);
		return null;
	}

	public static MongoClient getMongoClient() {
		if(MongoAccessClass.mongoClient == null)
			MongoAccessClass.mongoClient = new MongoClient(StaticMongoConfig.getDB_HOST_NAME()+":"+StaticMongoConfig.getDB_PORT_NUMBER());
		return MongoAccessClass.mongoClient;
	}

	@Override
	public Object getObjectByObjectID(ObjectId objectID) throws Exception {
		if(MongoAccessClass.getDatabase()!= null)
			if(this.getCollection() != null)
				return this.getCollection().find(new Document("_id",objectID));
			else
				throw new Exception("Mongo Collection not found");
		else
			throw new Exception("Mongo Database not found");
	}

	@Override
	public Document getObjectByKeyValuePair(String key, Object value) throws DataBaseException, ObjectNotFoundException {
		if(MongoAccessClass.getDatabase()!= null)
			if(this.getCollection() != null){
				Document document =  (Document) this.getCollection().find(new Document(key,value)).first();
				if(document == null){
					throw new ObjectNotFoundException();
				}
				else
					return document;
			}
			else
				throw new DataBaseException("Mongo Collection not found");
		else
			throw new DataBaseException("Mongo Database not found");
	}

	@Override
	public Object getObjectsByKeyValuePair(String key, Object value) throws Exception {
		if(MongoAccessClass.getDatabase()!= null)
			if(this.getCollection() != null)
				return this.getCollection().find(new Document(key,value));
			else
				throw new Exception("Mongo Collection not found");
		else
			throw new Exception("Mongo Database not found");
	}

	@Override
	public void updateObjectWithKey(String key,String value ,  Object updatedObject) throws JsonProcessingException, Exception {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(updatedObject);
		if(MongoAccessClass.getDatabase()!= null)
			if(this.getCollection() != null)
			{
				try{
					this.getCollection().replaceOne(new Document(key, value), Document.parse(jsonInString));
				}
				catch(Exception e){
					System.out.println(e);
				}
			}
			else
				throw new Exception("Mongo Collection not found");
		else
			throw new Exception("Mongo Database not found");
	}

	@Override
	public void deleteObjectWithKey(String key, Object value) throws Exception {
		if(MongoAccessClass.getDatabase()!= null)
			if(this.getCollection() != null)
				collection.deleteOne(new Document("_id",key));
			else
				throw new Exception("Mongo Collection not found");
		else
			throw new Exception("Mongo Database not found");
	}

	@Override
	public Boolean updateFieldOfObject(ObjectId objectIDKey, String fieldKey, Object fieldNewValue) throws Exception {
		Object object = this.getObjectByObjectID(objectIDKey);
		if (object == null)
			throw new Exception("Object to update not found");
		else
		{
			try{
				Document document = (Document) object;
				if(!document.containsKey(fieldKey)){
					//TODO:log that the field was inserted here.
				}
				ObjectMapper mapper = new ObjectMapper();
				document.put(fieldKey, mapper.writeValueAsString(fieldNewValue));
				this.getCollection().updateOne(new Document("_id" , objectIDKey), document);
				return true;
			}
			catch(Exception e){
				//TODO:return info that the field value did not update, log it here.
				return false;
			}
		}
	}

	@Override
	public Boolean deleteFieldOfObject(ObjectId objectIDKey, String fieldKey) throws Exception {
		Object object = this.getObjectByObjectID(objectIDKey);
		if (object == null)
			throw new Exception("Object to update not found");
		else
		{
			try{
				Document document = (Document) object;
				if(!document.containsKey(fieldKey)){
					return false;
				}
				document.remove(fieldKey);
				this.getCollection().updateOne(new Document("_id" , objectIDKey), document);
				return true;
			}
			catch(Exception e){
				return false;
				//TODO:return info that the field value did not update, log it here.
			}
		}
	}
	
	@Override
	public Boolean insertObject(Document document){
		try{
			this.getCollection().insertOne(document);
			return true;
		}
		catch(Exception e){
			System.out.println(e.getMessage()+ " message:class " + e.getClass());
		}
		
		return false;
	}
}
