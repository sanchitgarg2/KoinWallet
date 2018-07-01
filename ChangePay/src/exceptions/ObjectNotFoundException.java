package exceptions;

public class ObjectNotFoundException extends Exception {

	public ObjectNotFoundException() {
	}

	public ObjectNotFoundException(String message) {
		super(message);
	}

	public ObjectNotFoundException(Throwable cause) {
		super(cause);
	}

	public ObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ObjectNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
