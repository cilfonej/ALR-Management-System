package edu.wit.alr.web.security.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JWTAuthenticationProvider implements AuthenticationProvider {
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		// the function of this class/method is to take an Authentication object,
		// provided by some other part of the security-process, and construct a fully
		// populated Authentication object.
		
		// the function should also validate the details of the authentication and
		// throw an error if they invalid
		
		// null may be returned if the Provider is unable to process the provided Authentication object
		// otherwise an error should be thrown, or a fully-populated authentication object should be returned
		return auth;
	}

	public boolean supports(Class<?> clazz) {
		return JWTAuthenticationToken.class.isAssignableFrom(clazz);
	}
}
