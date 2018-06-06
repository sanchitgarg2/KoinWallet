package exceptions;

public class UserNotFoundException extends Exception {

	public UserNotFoundException() {
		super();
	}

	public UserNotFoundException(String message) {
		super(message);
	}
	public UserNotFoundException(Exception e) {
		super(e.getMessage());
		this.setStackTrace(e.getStackTrace());
	}

}
