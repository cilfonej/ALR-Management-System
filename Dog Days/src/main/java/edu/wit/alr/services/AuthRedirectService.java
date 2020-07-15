package edu.wit.alr.services;

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

//	@AuthenticationPrincipal
	
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
		AuthorizedRedirect redirect = findByKey(key);

		// setup authorization
		Account authorized = redirect.getAuthorization();
		String token = tokenProvider.createToken(authorized.getId(), redirect.getPermittedResources());
		sessionSecurity.pushToken(token);
		
		// setup extra-request data
		request.setAttribute(EXTRA_DATA_ATTRIBUTE, redirect.getRequestData());
		
		// send back forward address
		return redirect.getRedirect();
	}
	
	public String getRequestData(HttpServletRequest request) {
		return (String) request.getAttribute(EXTRA_DATA_ATTRIBUTE);
	}
	
	public AuthorizedRedirect findByKey(String key) { // TODO: possibly throw NoSuchElement + @404
		// TODO: validate redirect is "alive", remove if not
		return repositoy.findById(key).orElse(null);
	}
}
