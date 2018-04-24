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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@EnableAsync

public class GraphDataProcessor {
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static boolean initUser = true;
	public static Logger logger = LogManager.getFormatterLogger(GraphDataProcessor.class);
	static MongoCollection<Document> priceHistory = APIEndpointMapper.getDatabase().getCollection("PriceHistory");
	static MongoCollection<Document> priceHistoryBackup = APIEndpointMapper.getDatabase().getCollection("PriceHistoryBackup");
	static MongoCollection<Document> candleStickData = APIEndpointMapper.getDatabase().getCollection("CandleStickData");

	@SuppressWarnings("unchecked")
//	@Scheduled(fixedDelay = 6000)
	public void digestCurrencyData(){
		int DATA_POINT_TIME_LINE = 60;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);		

		for (String DATA_POINT_TIME_LINE_NAME:TimeLinetoPulseConverter.getDATA_POINT_TIME_LINE().keySet())
		{
			String lowerTimeLine = TimeLinetoPulseConverter.getLOWER_TIME_LINE().get(DATA_POINT_TIME_LINE_NAME);
			if(lowerTimeLine == null)
				continue;
			int ratio = TimeLinetoPulseConverter.DATA_POINT_TIME_LINE.get(DATA_POINT_TIME_LINE_NAME) / TimeLinetoPulseConverter.DATA_POINT_TIME_LINE.get(lowerTimeLine);

			//			if(LowerTimeLine == null){
			//				continue;
			//			}
			for(Currency c : Currency.getStaticCurrencyState().values()){

				//------------- The section to get the latest point of this timeline. Use that to filter the input data.
				JSONObject query1 = new JSONObject();
				query1.put("timeLine" ,new Document("$eq", DATA_POINT_TIME_LINE_NAME));
				query1.put("currencyCode",new Document("$eq", c.getCurrencyCode()));
				FindIterable<Document> MaxPoint = candleStickData.find(Document.parse(query1.toJSONString())).sort(new Document("refreshTime",-1)).limit(1);
				String maxOdate = "";
				for(Document abcd : MaxPoint){
					maxOdate = abcd.getString("RefreshTime");
				}
				//------------- End Section to get the latest point			

				JSONObject query = new JSONObject();
				query.put("timeLine" ,new Document("$eq", lowerTimeLine));
				query.put("currencyCode",new Document("$eq", c.getCurrencyCode()));
				if(maxOdate != null && !"".equals(maxOdate)) 
					query.put("openTimeStamp", new Document("$gt",Integer.parseInt(maxOdate)));
				else
					//TODO: Remove this else condition later. This was a filter for old, junk data.
					query.put("openTimeStamp", new Document("$gt", 151114000));
				FindIterable<Document> DBResult = candleStickData.find(Document.parse(query.toJSONString())).sort(new Document("refreshTime",-1)).limit(250);
				CandleStickDataPoint dataPoint;
				List<CandleStickDataPoint> graphData = new ArrayList<CandleStickDataPoint>();
				for( Document myDoc : DBResult ){
					try {
						dataPoint = mapper.readValue(myDoc.toJson(),CandleStickDataPoint.class);
					}  
					catch (Exception e) {
						continue;//break;
					}
					graphData .add(dataPoint);
				}
				//TODO:Delete the previous timeLine data - no right?
				//		Find out the 
				if(graphData.size() < ratio){
					//No Data found. 
					//TODO: Continue here or Asynchronously trigger a new thread to digest the data.
					continue;
				}
				else{
					Iterator<CandleStickDataPoint> inputData = graphData.iterator();
					List<CandleStickDataPoint> outputData = new ArrayList<CandleStickDataPoint>();
					CandleStickDataPoint mergedPoint , inputPoint;
					while(inputData.hasNext())
					{
						mergedPoint = new CandleStickDataPoint();
						mergedPoint.setTimeLine(DATA_POINT_TIME_LINE_NAME);
						inputPoint = inputData.next();
						int pointCount = 0;
						do {
							pointCount += 1;
							mergedPoint.merge(inputPoint);
							inputPoint = inputData.next();
						}
						while(inputData.hasNext() && 
								((mergedPoint.getCloseTimeStamp() - mergedPoint.getOpenTimeStamp()) < TimeLinetoPulseConverter.DATA_POINT_TIME_LINE.get(DATA_POINT_TIME_LINE_NAME)));
						// TODO: Drop / otherwise prevent from further processing the points obtained.
						// If the last few points are part of an incomplete senior dataPoint, do not consider any of them.
						System.out.println("This is a merged point from " + lowerTimeLine + " and " + pointCount + " points were merged.");
						if(pointCount >= ratio){
							outputData.add(mergedPoint);}
						else{
							//TODO: If the number of points is less than the optimal, the merged point should be dropped and the points should still be considered in the next run.
						}
					}
					//					String dataJson = (new ObjectMapper()).writeValueAsString(returnData);
				}
				//				------------------------------------------------------------------------------------------------------------------------------------------------------


			}
		}
		/*//		System.out.println(Currency.getCURRENCYSTATE().values());
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
				boolean shouldPrint = false;
				for( Document myDoc : DBResult){
					if(myDoc==null) {
						logger.log(Level.ERROR,"No History stored for this currency. " + c.getCurrencyCode());
						throw new CurrencyNotFoundException("Failed to locate Currency.");
					}
					CurrencySnapShot thisSnap = new CurrencySnapShot();
					//TODO: Remove the string replace later for performance improvements.
					System.out.print(myDoc.get("valueInINR") + "\t" + myDoc.get("refreshTime"));
					Pattern pattern = Pattern.compile("([0-9]+)\\.([0-9]+)");
					Matcher matcher = pattern.matcher(myDoc.toJson());
					Float abc = -1f;
					if(matcher.find())
						abc = Float.parseFloat(matcher.group(1)+"." +matcher.group(2));
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
						shouldPrint = true;
					}
					i2 += 1;
				}
				if(shouldPrint){
					System.out.println("Found " + i2 + " data points for this currency");	
					System.out.println("Divided into " + dataPointGroups.size() + " aggregated points ");
					System.out.println("Parsing each individual point.");
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					shouldPrint = false;
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
		}*/
	}
}
