package exceptions;

public class SessionNotFoundException extends Exception {
	public SessionNotFoundException(String message) {
		super(message);
		}
	public SessionNotFoundException() {
		super();
		}
}
