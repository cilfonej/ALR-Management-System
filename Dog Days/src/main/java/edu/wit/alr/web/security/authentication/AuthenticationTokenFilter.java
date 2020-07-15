package edu.wit.alr.web.security.authentication;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import edu.wit.alr.web.security.AccountPrincipalService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

/**
 * 	Filter attempts to extract a JWT from the current Http-Request's "Authorization" header
 * 	and validate its contents. If the token is found, and is valid, the filter generates
 *	a {@link JWTAuthenticationToken} and provides it as the authentication for this request.
 * 
 * 	@author cilfonej
 */
public class AuthenticationTokenFilter extends OncePerRequestFilter {

	@Autowired
	private AuthenticationTokenProvider provider;

	@Autowired
	private AccountPrincipalService service;
	
	@Autowired
	private SessionSecurityService sessionProvider;

	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain nextFilter) 
			throws IOException, ServletException {
		
		try {
			// attempt to parse and validate the provided token-data
			Jws<Claims> token = sessionProvider.getAuthorizationToken(request);
			
			// check if the token is still valid
			if(token != null) {
				// find the associated UserDetails for provided ID
				long authId = provider.getAuthorizedId(token);
				UserDetails userDetails = service.loadUserByAccountId(authId);
				
				// build an authorization for user
				JWTAuthenticationToken authentication = new JWTAuthenticationToken(token, userDetails);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			
		} catch(Exception e) {
			logger.error("Could not set user authentication in security context", e);
	
		} finally {
			nextFilter.doFilter(request, response);
		}
	}
}