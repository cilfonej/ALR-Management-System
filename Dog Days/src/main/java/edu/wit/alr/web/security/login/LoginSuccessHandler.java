package edu.wit.alr.web.security.login;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import edu.wit.alr.web.security.authentication.AuthenticationTokenProvider;
import edu.wit.alr.web.security.authentication.SessionSecurityService;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired private AuthenticationTokenProvider tokenProvider;
	@Autowired private SessionSecurityService sessionProvider;

	public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse response, Authentication auth) throws IOException {
		String targetUrl = determineTargetUrl(req, response, auth);

		// check if the response has already been sent
		if(response.isCommitted()) {
			// log error if so
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		// authentication complete, so remove tracking tokens
		clearAuthenticationAttributes(req, response);
		getRedirectStrategy().sendRedirect(req, response, targetUrl);
	}

	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
		String redirectUri = sessionProvider.getRedirectURI();
		if(redirectUri == null) redirectUri = "/";

		// TODO: I think this should go into a cookie
		String token = tokenProvider.createToken(auth);
		sessionProvider.pushToken(token);

		return UriComponentsBuilder.fromUriString(redirectUri).build().toUriString(); // .queryParam("token", token)
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		sessionProvider.recordRedirectLink(null);
	}
}