package edu.wit.alr.web.response;

public class NativeRedirectResponse extends Response {
	private String uri;
	
	protected NativeRedirectResponse(String uri) {
		super("native_redirect");
		this.uri = uri;
	}
	
	public String getUri() { return uri; }
}
