package edu.wit.alr.web.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public class JWTAuthenticationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = 3818159589771472241L;
	
	private UserDetails principal;
	private String credentials;
	
	public JWTAuthenticationToken(Jws<Claims> token, UserDetails user) {
		super(user.getAuthorities());
		
		this.principal = user;
		this.credentials = token.getSignature();
	}

	public Object getCredentials() { return credentials; }
	public Object getPrincipal() { return principal; }
	
	public void eraseCredentials() {
		super.eraseCredentials();
	}
}
