package edu.wit.alr.database.util;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PasswordSpec {
	@Column
	private String hash;
	
	@Column
	private String salt;

	// no-args constructor
	PasswordSpec() { }

	public PasswordSpec(String hash, String salt) {
		this.hash = hash;
		this.salt = salt;
	}

	public String getHash() { return hash; }
	public String getSalt() { return salt; }

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
}
