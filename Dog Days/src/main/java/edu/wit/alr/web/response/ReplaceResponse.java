package edu.wit.alr.web.response;

public class ReplaceResponse extends Response {
	private String query;
	private String replacementHTML;
	
	public ReplaceResponse(String query, String replacementHTML) {
		super("replace");
		
		this.query = query;
		this.replacementHTML = replacementHTML;
	}

	public String getQuery() { return query; }
	public String getReplacementHTML() { return replacementHTML; }
}