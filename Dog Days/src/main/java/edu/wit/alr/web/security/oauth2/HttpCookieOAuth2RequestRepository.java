package edu.wit.alr.web.security.oauth2;

import java.time.Duration;
import java.util.Base64;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import edu.wit.alr.web.util.CookieHelper;

/**
 *	Class used to temporary store information about User's {@link OAuth2AuthorizationRequest}
 *	inside of {@link javax.servlet.http.Cookie HTTP-Cookies}. This is done so that upon 
 *	authentication-callback the User can be identified and redirected to the appropriate page. 
 *
 * 	@author cilfonej
 */
@Component
public class HttpCookieOAuth2RequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
	// request must be completed before cookies expire
	private static final int COOKIE_LIFETIME = (int) Duration.ofMinutes(3).toSeconds();

	private static final String AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
	private static final String REDIRECT_URI_COOKIE_NAME = "redirect_uri";

	public String loadRedirectURI(HttpServletRequest request) {
		// search for the cookie, and if found return the redirect URI
		return CookieHelper.findCookie(request, REDIRECT_URI_COOKIE_NAME)
				.map(Cookie::getValue)
				.orElse("/"); // redirect to index/home when no redirect provided
	}
	
	public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
		// search for the cookie, and if found decode it into the OAurth2 Request
		return CookieHelper.findCookie(request, AUTHORIZATION_REQUEST_COOKIE_NAME)
				.map(cookie -> deserializeAuthorization(cookie.getValue()))
				.orElse(null);
	}

	public void saveAuthorizationRequest(OAuth2AuthorizationRequest authInfo, HttpServletRequest request, HttpServletResponse response) {
		// if no authentication-info was provided, remove the cookies
		if(authInfo == null) {
			CookieHelper.removeCookie(request, response, AUTHORIZATION_REQUEST_COOKIE_NAME);
			CookieHelper.removeCookie(request, response, REDIRECT_URI_COOKIE_NAME);
			return;
		}

		// get the redirect location from the URL
		String redirectUri = request.getParameter(REDIRECT_URI_COOKIE_NAME);
		if(redirectUri == null || redirectUri.isBlank()) redirectUri = "/"; // redirect to index/home when no redirect provided
		
		CookieHelper.addCookie(response, REDIRECT_URI_COOKIE_NAME, redirectUri, COOKIE_LIFETIME);
		CookieHelper.addCookie(response, AUTHORIZATION_REQUEST_COOKIE_NAME, serializeAuthorization(authInfo), COOKIE_LIFETIME);
	}

	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
		OAuth2AuthorizationRequest authInfo = loadAuthorizationRequest(request);
		
		// check if the value existed
		if(authInfo != null) {
			CookieHelper.removeCookie(request, response, AUTHORIZATION_REQUEST_COOKIE_NAME);
			CookieHelper.removeCookie(request, response, REDIRECT_URI_COOKIE_NAME);
		}
		
		return authInfo;
	}
	
	@Deprecated
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
		// cannot delete cookies, as we have no access to the HttpServletResponse
		return loadAuthorizationRequest(request);
	}
	
	private String serializeAuthorization(OAuth2AuthorizationRequest auth) {
		byte[] data = SerializationUtils.serialize(auth);
		return Base64.getUrlEncoder().encodeToString(data);
	}

	private OAuth2AuthorizationRequest deserializeAuthorization(String authData) {
		if(authData == null || authData.isEmpty()) return null;
		
		byte[] data = Base64.getUrlDecoder().decode(authData);
		return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(data);
	}
}