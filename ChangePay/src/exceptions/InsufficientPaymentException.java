package exceptions;

public class InsufficientPaymentException extends Exception {

	public InsufficientPaymentException() {
		super();
	}

	public InsufficientPaymentException(String message) {
		super(message);
	}

}
