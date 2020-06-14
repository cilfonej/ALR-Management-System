package edu.wit.alr.web.response;

import javax.validation.ValidationException;

public class InflationException extends ValidationException {
	private static final long serialVersionUID = 7521641490636590649L;

	public InflationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InflationException(String message) {
		super(message);
	}
}
