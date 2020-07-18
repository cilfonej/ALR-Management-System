package edu.wit.alr.web.security.authentication;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.WebApplicationContext;

import edu.wit.alr.web.security.WebSecurityConfig;
import edu.wit.alr.web.util.CookieHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@Service
public class SessionSecurityService {
	private static final String AUTH_TOKEN_COOKIE = "jws_token";

	@Autowired 
	private AuthenticationTokenProvider tokenProvider;
	
	@Autowired
	private ProxyData session;
	
	public void pushToken(String raw_token) {
		session.getTokens().add(raw_token);
	}
	
	public Jws<Claims> getAuthorizationToken(HttpServletRequest request) {
		Jws<Claims> token; 
		String token_raw;
	
		String path = request.getRequestURI();
		if(path == null) path = "/"; // if there's no path, assign root '/'

		final String URI = path;
		AntPathMatcher matcher = new AntPathMatcher();
		// check if no authorization is needed to access the resource
		if(List.of(WebSecurityConfig.RESOURCE_ANT_URIS).stream().anyMatch(ant -> matcher.match(ant, URI))) {
			// if so, set uri to null as it no longer matters
//			path = null;
			
			// don't _need_ and authentication to access this resource, so don't provide one 
			return null;
		}
		
		// first check for an 'Authorization' header
		token_raw = extractFromHeader(request);
		token = tokenProvider.validateRequest(token_raw, path);
		if(token != null) return token;
		
		// if header doesn't contain token, check cookies
		token_raw = extractFromCookie(request);
		token = tokenProvider.validateRequest(token_raw, path);
		if(token != null) return token;
		
		// TODO: this doesn't work in parallel :/
		Deque<String> tokens = session.getTokens();
		
		// last case, check the session for a valid token
		while(!tokens.isEmpty()) {
			// remove the next token from the stack
			token_raw = tokens.pop();
			token = tokenProvider.validateRequest(token_raw, path);

			// check if the token is valid
			if(token != null) {
				// if so, add it back to the stack
				tokens.push(token_raw);
				return token;
			}
			
			// otherwise, remove the token as it's no longer valid 
		}
		
		return null;
	}
	
	public String extractFromHeader(HttpServletRequest request) {
		// extract out the JWT from the Authorization-Header
		String tokenRaw = request.getHeader("Authorization");
		// check if header contains "Bearer <jwt>"
		if(tokenRaw == null || !tokenRaw.startsWith("Bearer ")) 
			return null;
		// strip "Bearer " leaving just JWT
		return tokenRaw.substring(7);
	}
	
	public String extractFromCookie(HttpServletRequest request) {
		return CookieHelper.findCookie(request, AUTH_TOKEN_COOKIE)
			.map(Cookie::getValue)
			.orElse(null);
	}
	
	public void recordRedirectLink(String uri) {
		session.redirectURI = uri;
	}
	
	public String getRedirectURI() {
		return session.redirectURI;
	}
	
	@Component
	@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
	static class ProxyData implements Serializable {
		private static final long serialVersionUID = -7996259074470530447L;
		
		public Deque<String> tokens;
		public String redirectURI;
		
		public Deque<String> getTokens() {
			if(tokens == null) 
				tokens = new ArrayDeque<>();
			return tokens;
		}
	}
}
