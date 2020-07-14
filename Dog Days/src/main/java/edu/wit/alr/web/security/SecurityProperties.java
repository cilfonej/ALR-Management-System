package edu.wit.alr.web.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("alr.security")
public class SecurityProperties {
	private Auth authorization;

	public Auth getAuth() { return authorization; }
	
	public static class Auth {
		private String token_secret;
		private long token_expiration;
		
		public String getTokenSecret() { return token_secret; }
		public long getTokenExpiration() { return token_expiration; }
		
		void setTokenSecret(String token_secret) {
			this.token_secret = token_secret;
		}
		
		void setTokenExpiration(long token_expiration) {
			this.token_expiration = token_expiration;
		}
	}

	public void setAuthorization(Auth authorization) {
		this.authorization = authorization;
	}
}
