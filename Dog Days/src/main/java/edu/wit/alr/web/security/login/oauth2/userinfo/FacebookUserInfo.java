package edu.wit.alr.web.security.login.oauth2.userinfo;

import java.util.Map;

public class FacebookUserInfo extends OAuth2UserInfo {
	
	public FacebookUserInfo(Map<String, Object> attributes) {
	    super(attributes);
	}

	public String getId() {
		return (String) attributes.get("id");
	}

	public String getName() {
		return (String) attributes.get("name");
	}

	public String getEmail() {
		return (String) attributes.get("email");
	}

	public String getImageUrl() {
		if(attributes.containsKey("picture")) {
			@SuppressWarnings("unchecked")
			Map<String, Object> pictureObj = (Map<String, Object>) attributes.get("picture");
			
			if(pictureObj.containsKey("data")) {
				@SuppressWarnings("unchecked")
				Map<String, Object> dataObj = (Map<String, Object>) pictureObj.get("data");
				
				if(dataObj.containsKey("url")) {
					return (String) dataObj.get("url");
				}
			}
		}
		
		return null;
	}
}