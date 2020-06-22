package edu.wit.alr.web.response;

public abstract class Response {
	private String action;
	
	protected Response(String action) {
		this.action = action;
	}
	
	public String getAction() { return action; }
}
