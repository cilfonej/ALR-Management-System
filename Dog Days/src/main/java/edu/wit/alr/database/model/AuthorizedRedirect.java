package edu.wit.alr.database.model;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * 	Database table used to store <code>links</code> to actions/redirects with a pre-authorized
 * 	account. A lifetime can be specified as {@link LocalDateTime DateTime} the link is valid 
 * 	until or as a one-time link the is invalid once accessed.
 * 
 *	@author cilfonej
 */
@Entity
public class AuthorizedRedirect {
	private static final Random RAND = new SecureRandom();
	private static final int ID_LENGTH = 64; // must be divisible by 16

	@Id
	@Column(unique = true, nullable = false)
	private String id;
	
	@Column(nullable = false)
	private String redirect;

	@Column
	private String permitted_paths;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Account authorization;
	
	@Column
	private LocalDateTime expiration;
	
	@Lob
	@Column
	private String request_data;

	// no-args constructor
	AuthorizedRedirect() { }
	
	public AuthorizedRedirect(@NotNull Account as, @NotNull String redirect) {
		this.id = generateKey();
		
		this.authorization = as;
		this.redirect = redirect;
	}

	public String getKey() { return id; }
	public String getRedirect() { return redirect; }
	
	public String getPermittedResources() { return permitted_paths; }
	public Account getAuthorization() { return authorization; }

	public LocalDateTime getExpiration() { return expiration; }
	public String getRequestData() { return request_data; }
	
	public boolean isExpired() {
		return expiration != null && expiration.isBefore(LocalDateTime.now());
	}
	
	public void addPermittedResource(String antPattern) {
		if(permitted_paths == null) permitted_paths = "";
		this.permitted_paths += antPattern + ",";
	}

	public void setExpiration(LocalDateTime expiration) {
		this.expiration = expiration;
	}

	public void setRequestData(String request_data) {
		this.request_data = request_data;
	}
	
	// this _should_ check the DB for another object with the same key
	// but the chances of that happening are astronomically low
	private static String generateKey() {
		Base64.Encoder encoder = Base64.getUrlEncoder();
		StringBuilder buffer = new StringBuilder(ID_LENGTH);
		byte[] raw_data = new byte[48];
		
		for(int i = 0; i < ID_LENGTH / 16; i ++) {
			RAND.nextBytes(raw_data);
			buffer.append(encoder.encodeToString(raw_data));
		}
		
		return buffer.toString();
	}
}
