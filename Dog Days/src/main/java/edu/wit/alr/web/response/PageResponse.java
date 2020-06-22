package edu.wit.alr.web.response;

public class PageResponse extends Response {
	private String url;
	private String pageHTML;
	
	public PageResponse(String url, String pageHTML) {
		super("redirect");
		
		this.url = url;
		this.pageHTML = pageHTML;
	}

	public String getUrl() { return url; }
	public String getPageHTML() { return pageHTML; }
}
