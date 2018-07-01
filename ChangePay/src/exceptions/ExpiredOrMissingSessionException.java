package exceptions;

public class ExpiredOrMissingSessionException extends Exception {

	public ExpiredOrMissingSessionException() {
	}

	public ExpiredOrMissingSessionException(String message) {
		super(message);
	}

	public ExpiredOrMissingSessionException(Throwable cause) {
		super(cause);
	}

	public ExpiredOrMissingSessionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpiredOrMissingSessionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
