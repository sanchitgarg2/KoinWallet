package CoinMonitor.APIService;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CandleStickDataPoint {
	String currencyCode;
	float low;
	float high;
	float open;
	float close;
	String openTimeStamp;
	String closeTimeStamp;
	String timeLine;

	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}	
	public String getOpenTimeStamp() {
		return openTimeStamp;
	}
	public void setOpenTimeStamp(String openTimeStamp) {
		this.openTimeStamp = openTimeStamp;
	}
	public String getCloseTimeStamp() {
		return closeTimeStamp;
	}
	public void setCloseTimeStamp(String closeTimeStamp) {
		this.closeTimeStamp = closeTimeStamp;
	}
	public String getTimeLine() {
		return timeLine;
	}
	public void setTimeLine(String timeLine) {
		this.timeLine = timeLine;
	}
	public float getLow() {
		return low;
	}
	public void setLow(float low) {
		this.low = low;
	}
	public float getHigh() {
		return high;
	}
	public void setHigh(float high) {
		this.high = high;
	}
	public float getOpen() {
		return open;
	}
	public void setOpen(float open) {
		this.open = open;
	}
	public float getClose() {
		return close;
	}
	public void setClose(float close) {
		this.close = close;
	}
	public void merge(CandleStickDataPoint inputPoint) {
		if(this.getClose() == 0 && this.getOpen() == 0 && this.getHigh() == 0 && this.getLow() == 0){
			this.setHigh(inputPoint.getHigh());
			this.setClose(inputPoint.getClose());
			this.setLow(inputPoint.getLow());
			this.setOpen(inputPoint.getOpen());
		}
		else{
			if(Long.parseLong(inputPoint.getOpenTimeStamp()) < Long.parseLong(this.getOpenTimeStamp())){
				this.setOpen(inputPoint.getOpen());
				this.setOpenTimeStamp(inputPoint.getOpenTimeStamp());}
			else if (Long.parseLong(inputPoint.getCloseTimeStamp()) > Long.parseLong(this.getCloseTimeStamp())){
				this.setClose(inputPoint.getClose());
				this.setCloseTimeStamp(inputPoint.getCloseTimeStamp());
			}
			this.setHigh(this.getHigh()<inputPoint.getHigh()?inputPoint.getHigh():this.getHigh());
			this.setLow(this.getLow()<inputPoint.getLow()?inputPoint.getLow():this.getLow());
		}
		
	}
	public Document toBSONDocument() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(this);
		return Document.parse(jsonInString);
	}
}
