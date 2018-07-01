package CoinMonitor.APIService.Exceptions;

public class UndefinedTimeLineException extends Exception {

	public UndefinedTimeLineException(String timeLine){
		super(timeLine + " is not a valid timeLine. Please get the DEVs to add support for this if you really want it.");
	}
}
