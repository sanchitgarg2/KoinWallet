package exceptions;

import java.io.IOException;

public class SystemInternalException extends Exception {

	public SystemInternalException(String message) {
		super(message);
	}

	public SystemInternalException(Exception e) {
		super(e.getMessage());
		this.setStackTrace(e.getStackTrace());
	}
}
