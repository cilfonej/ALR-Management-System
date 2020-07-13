package edu.wit.alr.web.security.oauth2.userinfo;

import java.util.Map;

/**
 * 	Base class for all OAuth2 User-types. Each OAuth2 service provides a different
 * 	set of attributes, with different names/keys. This class is used as a intermediate
 * 	layer to provided access to the fields needed by the application.
 * 
 * 	@author cilfonej
 */
public abstract class OAuth2UserInfo {
	protected Map<String, Object> attributes;

	public OAuth2UserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public abstract String getId();
	public abstract String getName();
	public abstract String getEmail();
	public abstract String getImageUrl();
}
