package edu.wit.alr.web.security.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JWTAuthenticationProvider implements AuthenticationProvider {
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		return auth;
	}

	public boolean supports(Class<?> clazz) {
		return JWTAuthenticationToken.class.isAssignableFrom(clazz);
	}
}
