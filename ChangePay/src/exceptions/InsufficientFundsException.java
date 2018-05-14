package exceptions;

public class InsufficientFundsException extends Exception {

	public InsufficientFundsException(String string) {
		super(string);
	}

	public InsufficientFundsException() {
		super();
	}

}
