package CoinMonitor.APIService;

import static com.mongodb.client.model.Filters.eq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

//import org.apache.logging.log4j.Logger;

import CoinMonitor.APIService.Currency.CurrencySnapShot;
import CoinMonitor.APIService.Exceptions.CurrencyNotFoundException;
import CoinMonitor.APIService.Exceptions.UserNotFoundException;

import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
@Configuration
@EnableScheduling
public class GraphDataProcessor {

	public static boolean initUser = true;
	public static Logger logger = LogManager.getFormatterLogger(GraphDataProcessor.class);
	static MongoCollection<Document> priceHistory = APIEndpointMapper.getDatabase().getCollection("PriceHistory");

	@Scheduled(fixedDelay = 10000)
	public void digestCurrencyData(Currency c){
		final int DATA_POINT_DEFAULT_GROUP_SIZE = 6;
		ObjectMapper mapper = new ObjectMapper();
		List<CurrencySnapShot> allDataPoints  = new ArrayList<CurrencySnapShot>();
		List<List<CurrencySnapShot>> dataPointGroups = new ArrayList<List<CurrencySnapShot>>();
		try {	
			List<CurrencySnapShot> localDataPointsGroup = new ArrayList<CurrencySnapShot>();
			//TODO:sort the database since we do not have any way to get the most recent values.
			for( Document myDoc :priceHistory.find(eq("code",c.getCurrencyCode()))){
				CurrencySnapShot thisSnap = new CurrencySnapShot();
				if(myDoc==null) {
					logger.log(Level.WARN,"No History stored for this currency. " + c.getCurrencyCode());
					throw new CurrencyNotFoundException("Failed to locate Currency.");
				}
				myDoc.remove("_id");
				thisSnap = mapper.readValue(myDoc.toJson(),CurrencySnapShot.class);
				allDataPoints.add(thisSnap);
				localDataPointsGroup.add(thisSnap);
				if( localDataPointsGroup.size() >= DATA_POINT_DEFAULT_GROUP_SIZE ){
					dataPointGroups.add(localDataPointsGroup);
					localDataPointsGroup = new ArrayList<CurrencySnapShot>();
				}
			}
			for (List<CurrencySnapShot> dataPointsGroup : dataPointGroups){
				CandleStickDataPoint dataPoint = new CandleStickDataPoint();
				for(CurrencySnapShot snap:dataPointsGroup){
					if(dataPoint.getLow() == 0 || snap.getValueInINR()<dataPoint.getLow())
						dataPoint.setLow(snap.getValueInINR());
					if(dataPoint.getHigh() == 0 || snap.getValueInINR() > dataPoint.getHigh())
						dataPoint.setHigh(snap.getValueInINR());
					if(dataPoint.getOpen() == 0)
						dataPoint.setOpen(snap.getValueInINR());
					dataPoint.setClose(snap.getValueInINR());
				}
			}
		}
		catch(Exception e){
			logger.error(e.getMessage());
		}
	}


}
