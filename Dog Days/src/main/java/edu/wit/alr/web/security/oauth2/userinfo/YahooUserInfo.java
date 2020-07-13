package edu.wit.alr.web.security.oauth2.userinfo;

import java.util.Map;

// TODO: lookup Yahoo OAuth2 response JSON docs
public class YahooUserInfo extends OAuth2UserInfo {

	public YahooUserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	public String getId() {
		return null;
	}

	public String getName() {
		return null;
	}

	public String getEmail() {
		return null;
	}

	public String getImageUrl() {
		return null;
	}
}
