package CoinMonitor.APIService;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;


//import org.apache.logging.log4j.Logger;

import CoinMonitor.APIService.Currency.CurrencySnapShot;
import CoinMonitor.APIService.Exceptions.CurrencyNotFoundException;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@EnableAsync

public class GraphDataProcessor2 {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static boolean initUser = true;
	public static Logger logger = LogManager.getFormatterLogger(GraphDataProcessor.class);
	static MongoCollection<Document> priceHistory = APIEndpointMapper.getDatabase().getCollection("PriceHistory");
	static MongoCollection<Document> priceHistoryBackup = APIEndpointMapper.getDatabase().getCollection("PriceHistoryBackup");
	static MongoCollection<Document> candleStickData = APIEndpointMapper.getDatabase().getCollection("CandleStickData");

	
	private void printData(int i2, int dataPointsGroupsSize){
		System.out.println("Found " + i2 + " data points for this currency");	
		System.out.println("Divided into " + dataPointsGroupsSize + " aggregated points ");
		System.out.println("Parsing each individual point.");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
	
	@SuppressWarnings("unchecked")
	@Scheduled(fixedDelay = 6000)
	public void digestCurrencyData(){
		int DATA_POINT_TIME_LINE = 60;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);		
		for(Currency c : Currency.getCURRENCYSTATE().values()){
			System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println("Processing for " + c.getCurrencyCode() + " ");
			try {
				//				List<CurrencySnapShot> allDataPoints  = new ArrayList<CurrencySnapShot>();
				List<List<CurrencySnapShot>> dataPointGroups = new ArrayList<List<CurrencySnapShot>>();
				List<CurrencySnapShot> localDataPointsGroup = new ArrayList<CurrencySnapShot>();
				List<org.bson.types.ObjectId> removeIds = new ArrayList<>();
				FindIterable<Document> DBResult = priceHistory.find(eq("currencyCode",c.getCurrencyCode())).sort(new Document("refreshTime",-1)).limit(250);
				int i2 = 0;
				boolean hasDeletedRecords = false;
				int i=0;
				for( Document myDoc : DBResult){
					i++;
					if(myDoc==null) {
						logger.log(Level.ERROR,"No History stored for this currency. " + c.getCurrencyCode());
						throw new CurrencyNotFoundException("Failed to locate Currency.");
					}
					CurrencySnapShot thisSnap = new CurrencySnapShot();
					//TODO: Remove the string replace later for performance improvements.
					System.out.print(myDoc.get("valueInINR") + "\t" + myDoc.get("refreshTime"));
					Pattern numberPattern = Pattern.compile("([0-9]+)\\.([0-9]+)");
					Matcher numberPatternMatcher = numberPattern.matcher(myDoc.toJson());
					Float abc = -1f;
					if(numberPatternMatcher.find())
						abc = Float.parseFloat(numberPatternMatcher.group(1)+"." +numberPatternMatcher.group(2));
					thisSnap = mapper.readValue(myDoc.toJson().replaceAll("RefreshTime", "refreshTime"),CurrencySnapShot.class);
					thisSnap.setValueInINR(abc);
					localDataPointsGroup.add(thisSnap);

					removeIds.add((org.bson.types.ObjectId)myDoc.get("_id"));
					//					priceHistory.deleteOne(myDoc);
					//					myDoc.remove("_id");
					//					priceHistoryBackup.insertOne(myDoc);
					if( Math.abs( localDataPointsGroup.get(0).getRefreshTime() - localDataPointsGroup.get(localDataPointsGroup.size()-1).getRefreshTime()) >= DATA_POINT_TIME_LINE ){
						dataPointGroups.add(localDataPointsGroup);
						localDataPointsGroup = new ArrayList<CurrencySnapShot>();
						priceHistory.deleteMany(new Document("_id", new Document("$in" , removeIds)));
						removeIds = new ArrayList<org.bson.types.ObjectId>();
						printData(i2,dataPointGroups.size());
					}
					i2 += 1;
				}
				for (List<CurrencySnapShot> dataPointsGroup : dataPointGroups){
					System.out.println("This candleStick point is a merge of " + dataPointsGroup.size());
					CandleStickDataPoint dataPoint = new CandleStickDataPoint();
					dataPoint.setCurrencyCode(c.getCurrencyCode());
					dataPoint.setTimeLine("MINUTE");
					for(CurrencySnapShot snap:dataPointsGroup){
						if(dataPoint.getLow() == 0 || snap.getValueInINR() < dataPoint.getLow())
							dataPoint.setLow(snap.getValueInINR());
						if(dataPoint.getHigh() == 0 || snap.getValueInINR() > dataPoint.getHigh())
							dataPoint.setHigh(snap.getValueInINR());
						if(dataPoint.getClose() == 0){
							dataPoint.setClose(snap.getValueInINR());
							dataPoint.setCloseTimeStamp(snap.getRefreshTime());
						}
						dataPoint.setOpen(snap.getValueInINR());
						dataPoint.setOpenTimeStamp(snap.getRefreshTime());
					}
					System.out.println(dataPoint);
					candleStickData.insertOne(dataPoint.toBSONDocument());
				}
			}
			catch(Exception e){
				logger.error(e.getMessage());
			}
			System.out.println("Data digested for " + c.getCurrencyCode());
		}
		
	}
	
}
