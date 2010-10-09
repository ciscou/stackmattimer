package com.rubikaz.cisco.tuenti;

public class TuentiException extends Exception {
	private static final long serialVersionUID = 1L;

	public TuentiException(String message) {
	  super(message);
	}
	
	public TuentiException(String message, Throwable cause) {
	  super(message, cause);
	}
}
