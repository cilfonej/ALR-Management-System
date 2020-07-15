package edu.wit.alr.web.security.authentication;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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

	@Autowired
	private SecurityProperties properties;

	public String createToken(Authentication authentication) {
		UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
		return createToken(principal.getId());
	}
	
	public String createToken(long account_id) {
		Instant now = Instant.now();
		Instant expires = now.plusSeconds(properties.getAuth().getTokenExpiration());

		return Jwts.builder()
					.setSubject(String.valueOf(account_id))
					.setIssuedAt(Date.from(now))
					.setExpiration(Date.from(expires))
					.signWith(SignatureAlgorithm.HS512, properties.getAuth().getTokenSecret())
				.compact();
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
