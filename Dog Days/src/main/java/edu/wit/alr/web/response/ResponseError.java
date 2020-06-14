package edu.wit.alr.web.response;

public class ResponseError {
	public static enum ErrorLevel { Info, Warning, Error, Fatal }

	private ErrorLevel level;
	private String fieldName;
	private String message;
	
	private ResponseError(ErrorLevel level, String field, String message) {
		this.level = level;
		this.fieldName = field;
		this.message = message;
	}
	
	public static ResponseError error(String message) {
		return new ResponseError(ErrorLevel.Error, null, message);
	}
}
