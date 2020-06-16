package edu.wit.alr.services.inflators;

public class InflationException extends RuntimeException {
	private static final long serialVersionUID = -6028295949585104623L;

	public InflationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InflationException(String message) {
		super(message);
	}

	public InflationException(Throwable cause) {
		super(cause);
	}
}
