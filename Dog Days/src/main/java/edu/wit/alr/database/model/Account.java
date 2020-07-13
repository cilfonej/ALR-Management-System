package edu.wit.alr.database.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import edu.wit.alr.database.util.PasswordSpec;
import edu.wit.alr.web.security.AuthProviderType;

@Entity
public class Account {

	@Id
	@GeneratedValue
	@Column(unique = true, nullable = false)
	private int id;
	
	@ManyToOne
	@JoinColumn
	private Person person;
	
	@Column
	@Enumerated(EnumType.STRING)
    private AuthProviderType auth_service;
	
	/** ID used to store external user lookup */
	@Column
	private String external_id;

	/** Username (typically an email) of the account 
	 * 	field is only used when auth_service is 'local'
	 */
	@Column(unique = true)
	private String username;

	/** Password fields only used when auth_service is 'local' */
	@Embedded
	private PasswordSpec password;
	
	// no-args constructor
	Account() { }
	
	public Account(Person person) {
		this.person = person;
	}

	public int getId() { return id; }
	public Person getPerson() { return person; }

	public AuthProviderType getAuthService() { return auth_service; }

	public String getExternalId() { return external_id; }

	public String getUsername() { return username; }
	public PasswordSpec getPassword() { return password; }
	
	public Account withLocalAuthentication(@NotNull String username, PasswordSpec password) {
		this.auth_service = AuthProviderType.local;
		
		this.username = username;
		this.password = password;
		
		this.external_id = null;
		return this;
	}
	
	public Account withExternalAuthentication(@NotNull AuthProviderType authority, @NotNull String externalID) {
		if(authority == AuthProviderType.local) 
			throw new IllegalArgumentException("'local' is not a valid external Authority");
		this.auth_service = authority;
		
		this.external_id = externalID;
		
		this.username = null;
		this.password = null;
		return this;
	}
}
