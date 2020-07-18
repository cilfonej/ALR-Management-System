package edu.wit.alr.web.security.login.oauth2.userinfo;

import java.util.Map;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

import edu.wit.alr.web.security.AuthProviderType;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
    	// TODO: validate these name checks work
    
        if(registrationId.equalsIgnoreCase(AuthProviderType.Google.toString())) {
            return new GoogleUserInfo(attributes);
            
        } else if (registrationId.equalsIgnoreCase(AuthProviderType.Facebook.toString())) {
            return new FacebookUserInfo(attributes);
            
        } else if (registrationId.equalsIgnoreCase(AuthProviderType.Yahoo.toString())) {
            return new YahooUserInfo(attributes);
            
        } else {
            throw new InternalAuthenticationServiceException("Unsupported OAuth2 Service!");
        }
    }
}