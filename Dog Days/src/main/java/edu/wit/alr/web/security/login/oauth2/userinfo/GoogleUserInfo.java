package edu.wit.alr.web.security.login.oauth2.userinfo;

import java.util.Map;

public class GoogleUserInfo extends OAuth2UserInfo {

	public GoogleUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

	public String getId() {
		return (String) attributes.get("sub");
	}

	public String getName() {
		return (String) attributes.get("name");
	}

	public String getEmail() {
		return (String) attributes.get("email");
	}

	public String getImageUrl() {
		return (String) attributes.get("picture");
	}
}