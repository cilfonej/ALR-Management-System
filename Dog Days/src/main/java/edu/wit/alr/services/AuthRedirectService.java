package edu.wit.alr.services;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wit.alr.database.model.Account;
import edu.wit.alr.database.model.AuthorizedRedirect;
import edu.wit.alr.database.repository.AuthorizedRedirectRepository;
import edu.wit.alr.web.security.authentication.AuthenticationTokenProvider;
import edu.wit.alr.web.security.authentication.SessionSecurityService;

@Service
public class AuthRedirectService {
	public static final String EXTRA_DATA_ATTRIBUTE = "redirect-data";
	
	@Autowired
	private AuthorizedRedirectRepository repositoy;

	@Autowired private AuthenticationTokenProvider tokenProvider;
	@Autowired private SessionSecurityService sessionSecurity;

	/**
	 * 	Takes in a redirect <code>key</code> and the base <code>request</code>.
	 * 	Method setups session-security token for {@link Account} specified in 
	 * 	<code>Redirect</code>, with appropriate restrictions. The extra-data,
	 * 	pre-specified in the <code>Redirect</code>, is then added to the 
	 * 	{@link #EXTRA_DATA_ATTRIBUTE} attribute of the {@link HttpServletRequest Request}.
	 * 
	 * 	@param key id-string of the AuthorizedRedirect
	 * 	@param request base http-request
	 * 
	 * 	@return redirect-path
	 */
	public String setupForward(String key, HttpServletRequest request) {
		AuthorizedRedirect redirect = findByKey(key); // TODO: error page when redirect is null

		// setup authorization
		Account authorized = redirect.getAuthorization();
		String token = tokenProvider.createToken(authorized.getId(), redirect.getPermittedResources());
		sessionSecurity.pushToken(token);
		
		// setup extra-request data
		request.setAttribute(EXTRA_DATA_ATTRIBUTE, redirect.getRequestData());
		// TODO: sign data and store it in an attribute
		
		// send back forward address
		return redirect.getRedirect();
	}
	
	public String getRequestData(HttpServletRequest request) {
		// TODO: validate that this request came from the sever (ie. forward:/)
		return (String) request.getAttribute(EXTRA_DATA_ATTRIBUTE);
	}
	
	public boolean isDataValid() {
		// TODO: validate signature to prove data is safe to use
		return true;
	}
	
	public AuthorizedRedirect findByKey(String key) { // TODO: possibly throw NoSuchElement + @404
		AuthorizedRedirect redirect = repositoy.findById(key).orElse(null);
		if(redirect == null) return null;
		
		// check if this is a 1-off link
		if(redirect.getExpiration() == null) {
			repositoy.delete(redirect); // TODO: maybe set the expiration to now + 10minuets instead
			return redirect;
		}
		
		// check if the redirect has expired
		if(redirect.getExpiration().isBefore(LocalDateTime.now())) {
			repositoy.delete(redirect); // remove the expirered link
			return null;
		}
		
		return redirect;
	}
}
