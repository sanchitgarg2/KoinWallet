package Coinclasses;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CandleStickDataPoint implements Comparable<CandleStickDataPoint>{
	@Override
	public String toString() {
		return String.format(
				"CandleStickDataPoint [currencyCode=%s, low=%s, high=%s, open=%s, close=%s, openTimeStamp=%s, closeTimeStamp=%s, timeLine=%s]",
				currencyCode, low, high, open, close, openTimeStamp, closeTimeStamp, timeLine);
	}
	String currencyCode;
	float low;
	float high;
	float open;
	float close;
	long openTimeStamp;
	long closeTimeStamp;
	String timeLine;

	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public long getOpenTimeStamp() {
		return openTimeStamp;
	}
	public void setOpenTimeStamp(Long openTimeStamp) {
		try{
			this.openTimeStamp = openTimeStamp;
		}
		catch(Exception e){
			this.openTimeStamp = -1;
		}
	}
	public long getCloseTimeStamp() {
		return closeTimeStamp;
	}
	public void setCloseTimeStamp(Long closeTimeStamp) {
		try{
			this.closeTimeStamp = closeTimeStamp;
		}
		catch(Exception e){
			this.closeTimeStamp = -1;
		}
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
			this.setCurrencyCode(inputPoint.getCurrencyCode());
			this.setTimeLine(inputPoint.getTimeLine());
			this.setHigh(inputPoint.getHigh());
			this.setClose(inputPoint.getClose());
			this.setLow(inputPoint.getLow());
			this.setOpen(inputPoint.getOpen());
			this.setCloseTimeStamp(inputPoint.getCloseTimeStamp());
			this.setOpenTimeStamp(inputPoint.getOpenTimeStamp());
		}
		else{
			if(inputPoint.getOpenTimeStamp() < this.getOpenTimeStamp()){
				this.setOpen(inputPoint.getOpen());
				this.setOpenTimeStamp(inputPoint.getOpenTimeStamp());}
			else if (inputPoint.getCloseTimeStamp() > this.getCloseTimeStamp()){
				this.setClose(inputPoint.getClose());
				this.setCloseTimeStamp(inputPoint.getCloseTimeStamp());
			}
			this.setHigh(this.getHigh()<inputPoint.getHigh()?inputPoint.getHigh():this.getHigh());
			this.setLow(this.getLow()<inputPoint.getLow()?inputPoint.getLow():this.getLow());
		}

	}
	public int compareTo(CandleStickDataPoint otherPoint) {

		long otherTimeStamp = ((CandleStickDataPoint) otherPoint).getOpenTimeStamp();

		//ascending order
		return (int) (this.getOpenTimeStamp() - otherTimeStamp);

		//descending order
		//return compareQuantity - this.quantity;

	}
	/*public Document toBSONDocument() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(this);
		return Document.parse(jsonInString);
	}
	*/
}
