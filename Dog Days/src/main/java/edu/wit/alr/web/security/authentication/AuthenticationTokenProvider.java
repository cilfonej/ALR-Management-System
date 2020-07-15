package edu.wit.alr.web.security.authentication;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import edu.wit.alr.web.security.SecurityProperties;
import edu.wit.alr.web.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class AuthenticationTokenProvider {
	private static final String CLAIM__ALLOW_URI = "permitted_uri";
	
	@Autowired
	private SecurityProperties properties;

	public String createToken(Authentication authentication) {
		UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
		return createToken(principal.getId(), null);
	}
	
	/**
	 *	@param account_id the id of the Account being authenticated
	 *	@param allowedURIs a comma-separated list of paths, or ant-patterns, to allow access to, or null for no restriction
	 */
	public String createToken(long account_id, String allowedPaths) {
		Instant now = Instant.now();
		Instant expires = now.plusSeconds(properties.getAuth().getTokenExpiration());

		return Jwts.builder()
					.setSubject(String.valueOf(account_id))
					.claim(CLAIM__ALLOW_URI, allowedPaths)
					.setIssuedAt(Date.from(now))
					.setExpiration(Date.from(expires))
					.signWith(SignatureAlgorithm.HS512, properties.getAuth().getTokenSecret())
				.compact();
	}
	
	/**
	 * 	Check if the provided token is valid and allows access to the requested resource-path
	 */
	public Jws<Claims> validateRequest(String authToken, String uri) {
		Jws<Claims> token = validateToken(authToken);
		if(token == null) return null; // invalid token, reject access
		
		String uri_filters = token.getBody().get(CLAIM__ALLOW_URI, String.class);
		if(uri_filters == null) return token; // no-restriction, allow access
	
		AntPathMatcher matcher = new AntPathMatcher();
		String[] filters = uri_filters.split(",");
				
		for(String filter : filters) {
			filter = filter.trim();
			if(filter.isEmpty()) continue;
			
			// if any of the provided filters match
			if(matcher.match(filter, uri)) {
				return token; // allow access
			}
		}
		
		return null;
	}
	
	public Jws<Claims> validateToken(String authToken) {
		try {
			if(authToken == null || authToken.isBlank()) 
				// TODO: maybe throw exception to trigger similar catch block
				return null;
				//throw new IllegalArgumentException("Provided token was blank");
			
			// attempt to parse the JW-Token, if error occurs token is invalid
			return Jwts.parser().setSigningKey(properties.getAuth().getTokenSecret()).parseClaimsJws(authToken);
			
		} catch(SignatureException | MalformedJwtException | ExpiredJwtException e) {
			// TODO: Log bad authorization attempt
		} catch(UnsupportedJwtException | IllegalArgumentException e) {
		}
		
		return null;
	}

	public long getAuthorizedId(Jws<Claims> token) {
		Claims claims = token.getBody();
		return Long.parseLong(claims.getSubject());
	}
}
