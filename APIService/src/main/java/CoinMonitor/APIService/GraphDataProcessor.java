package CoinMonitor.APIService;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class GraphDataProcessor {
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static boolean initUser = true;
	public static Logger logger = LogManager.getFormatterLogger(GraphDataProcessor.class);
	static MongoCollection<Document> priceHistory = APIEndpointMapper.getDatabase().getCollection("PriceHistory");
	static MongoCollection<Document> candleStickData = APIEndpointMapper.getDatabase().getCollection("CandleStickData");
	
	@Scheduled(fixedDelay = 10000)
	public void digestCurrencyData(){
		final int DATA_POINT_TIME_LINE = 60000;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		for(Currency c : Currency.getCURRENCYSTATE().values()){
			try {
				List<CurrencySnapShot> allDataPoints  = new ArrayList<CurrencySnapShot>();
				List<List<CurrencySnapShot>> dataPointGroups = new ArrayList<List<CurrencySnapShot>>();
				List<CurrencySnapShot> localDataPointsGroup = new ArrayList<CurrencySnapShot>();
				for( Document myDoc :priceHistory.find(eq("code",c.getCurrencyCode())).sort(new Document("odate",1)).limit(250) ){
					CurrencySnapShot thisSnap = new CurrencySnapShot();
					if(myDoc==null) {
						logger.log(Level.WARN,"No History stored for this currency. " + c.getCurrencyCode());
						throw new CurrencyNotFoundException("Failed to locate Currency.");
					}
//					myDoc.remove("_id");
//					mapper.set
					thisSnap = mapper.readValue(myDoc.toJson(),CurrencySnapShot.class);
					allDataPoints.add(thisSnap);
					localDataPointsGroup.add(thisSnap);
					priceHistory.deleteOne(myDoc);
					if( Math.abs(Long.parseLong(localDataPointsGroup.get(0).getRefreshTime()) - Long.parseLong(localDataPointsGroup.get(localDataPointsGroup.size()-1).getRefreshTime())) >= DATA_POINT_TIME_LINE ){
						dataPointGroups.add(localDataPointsGroup);
						localDataPointsGroup = new ArrayList<CurrencySnapShot>();
					}
				}
				for (List<CurrencySnapShot> dataPointsGroup : dataPointGroups){
					CandleStickDataPoint dataPoint = new CandleStickDataPoint();
//					long odate;
					dataPoint.setCurrencyCode(c.getCurrencyCode());
					dataPoint.setTimeLine("MINUTE");
					for(CurrencySnapShot snap:dataPointsGroup){
						if(dataPoint.getLow() == 0 || snap.getValueInINR()<dataPoint.getLow())
							dataPoint.setLow(snap.getValueInINR());
						if(dataPoint.getHigh() == 0 || snap.getValueInINR() > dataPoint.getHigh())
							dataPoint.setHigh(snap.getValueInINR());
						if(dataPoint.getOpen() == 0){
							dataPoint.setOpen(snap.getValueInINR());
							dataPoint.setOpenTimeStamp(snap.getRefreshTime());
							}
						dataPoint.setClose(snap.getValueInINR());
						dataPoint.setCloseTimeStamp(snap.getRefreshTime());
					}
					candleStickData.insertOne(dataPoint.toBSONDocument());
				}
			}
			catch(Exception e){
				logger.error(e.getMessage());
			}
		}
	}
}
