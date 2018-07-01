package CoinMonitor.APIService;

import java.util.HashMap;

import org.bson.Document;

import CoinMonitor.APIService.Exceptions.UndefinedTimeLineException;

public class TimeLinetoPulseConverter {

	static HashMap<String,String> mapping = null;
	static HashMap<String,Long> timeLineMapping = null;// new HashMap<String, Long>();
	static HashMap<String,Integer> DATA_POINT_TIME_LINE = null;
	static HashMap<String,String> LOWER_TIME_LINE = null;
	public static String getMapping(String timeLine) throws UndefinedTimeLineException{
		if(mapping == null)
			initMapping();
		if(mapping.containsKey(timeLine)){
			return mapping.get(timeLine);
		}
		else{
			throw new UndefinedTimeLineException(timeLine);
		}
	}
	public static long getStartTime(String timeLine) throws UndefinedTimeLineException{
		if(timeLineMapping == null)
			initTimeLineMapping();
		if(timeLineMapping.containsKey(timeLine)){
			long interval = timeLineMapping.get(timeLine);
			if (interval == -1)
				return -1;
			else
				return System.currentTimeMillis()/1000 - interval;
		}
		else{
			throw new UndefinedTimeLineException(timeLine);
		}
	}

	private static void initTimeLineMapping(){
		timeLineMapping = new HashMap<String, Long>();
		timeLineMapping.put("ALL_TIME" , new Long(-1));
		timeLineMapping.put("ONE_YEAR" , new Long(31104000));
		timeLineMapping.put("SIX_MONTHS" , new Long(15552000));
		timeLineMapping.put("THREE_MONTHS" , new Long(7776000));
		timeLineMapping.put("ONE_MONTH" , new Long(2592000));
		timeLineMapping.put("ONE_WEEK" , new Long(604800));
		timeLineMapping.put("ONE_DAY" , new Long(86400));
		timeLineMapping.put("SIX_HOURS" , new Long(21600));
		timeLineMapping.put("THREE_HOURS" , new Long(10800));
		timeLineMapping.put("ONE_HOUR" , new Long(3600));
	}
	
	private static void initMapping() {
		mapping = new HashMap<String, String>();
		mapping.put("ALL_TIME","THREE_DAYS");
		mapping.put("ONE_YEAR","THREE_DAYS");
		mapping.put("SIX_MONTHS","DAY");
		mapping.put("THREE_MONTHS","DAY");
		mapping.put("ONE_MONTH","THREE_HOURS");
		mapping.put("ONE_WEEK","HOUR");
		mapping.put("ONE_DAY","SIX_MINUTES");
		mapping.put("SIX_HOURS","MINUTES");
		mapping.put("THREE_HOURS","MINUTE");
		mapping.put("ONE_HOUR","MINUTE");	
		
		DATA_POINT_TIME_LINE = new HashMap<String,Integer>();
		DATA_POINT_TIME_LINE.put("MINUTE", 60);
		DATA_POINT_TIME_LINE.put("SIX_MINUTES", 360);
		DATA_POINT_TIME_LINE.put("HOUR", 3600);
		DATA_POINT_TIME_LINE.put("THREE_HOURS", 10800);
		DATA_POINT_TIME_LINE.put("DAY", 86400);
		DATA_POINT_TIME_LINE.put("THREE_DAYS", 259200);
		
		LOWER_TIME_LINE = new HashMap<String,String>();
		LOWER_TIME_LINE.put("MINUTE", null);
		LOWER_TIME_LINE.put("SIX_MINUTES", "MINUTE");
		LOWER_TIME_LINE.put("HOUR", "SIX_MINUTES");
		LOWER_TIME_LINE.put("THREE_HOURS","HOUR");
		LOWER_TIME_LINE.put("DAY", "THREE_HOURS");
		LOWER_TIME_LINE.put("THREE_DAYS", "DAY");
//		DA
	}
	public static HashMap<String,Integer> getDATA_POINT_TIME_LINE() {
		if(DATA_POINT_TIME_LINE == null)
			initMapping();
		return DATA_POINT_TIME_LINE;
	}
	public static HashMap<String,String> getLOWER_TIME_LINE(){
		if(LOWER_TIME_LINE == null)
			initMapping();
		return LOWER_TIME_LINE;
	}
	
}
