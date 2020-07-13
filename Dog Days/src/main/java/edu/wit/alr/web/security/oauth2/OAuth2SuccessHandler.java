package edu.wit.alr.web.security.oauth2;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import edu.wit.alr.web.security.authentication.AuthenticationTokenProvider;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired private AuthenticationTokenProvider tokenProvider;
//	@Autowired private AppProperties appProperties;
	@Autowired private HttpCookieOAuth2RequestRepository repository;

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
		String redirectUri = repository.loadRedirectURI(request);

		// TODO: ??? not sure what redirectUriis for now ???
		// check to make sure the redirect-link is on a supported domain
		if(redirectUri != null && !isAuthorizedRedirectUri(redirectUri)) {
			// TODO: What kind of exception?  was BadRequestException @ResponseStatus(HttpStatus.BAD_REQUEST)
			throw new RuntimeException("Unauthorized Redirect URI; can't proceed with the authentication");
		}

		String token = tokenProvider.createToken(auth);

		return UriComponentsBuilder.fromUriString(redirectUri).queryParam("token", token).build().toUriString();
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		repository.removeAuthorizationRequest(request, response);
	}

	private boolean isAuthorizedRedirectUri(String uri) {
		URI clientRedirectUri = URI.create(uri);
		return true;
		// TODO: man I don't know
//		return appProperties.getOauth2().getAuthorizedRedirectUris().stream().anyMatch(authorizedRedirectUri -> {
//			// Only validate host and port. Let the clients use different paths if they want to
//			URI authorizedURI = URI.create(authorizedRedirectUri);
//			if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
//					&& authorizedURI.getPort() == clientRedirectUri.getPort()) {
//				return true;
//			}
//			return false;
//		});
	}
}