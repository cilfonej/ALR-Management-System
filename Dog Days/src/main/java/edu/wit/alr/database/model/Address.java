package edu.wit.alr.database.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Address {
	
	@Id
	@GeneratedValue
	@Column(unique = true, nullable = false)
	private int id;

	@Column(nullable = false)
	private String street_address;

	@Column(nullable = false, length = 72)
	private String city;

	@Column(nullable = false, length = 2)
	private String state;
	
	@Column(length = 16)
	private String postal_code;

	@Column(length = 128)
	private String country;
	
	// no-args constructor
	public Address() { }
	
	public Address(String street, String city, String state) {
		this(street, city, state, null, "United States");
	}
	
	public Address(String street, String city, String state, String postalCode, String country) {
		this.street_address = street;
		this.city = city;
		this.state = state;
		this.postal_code = postalCode;
		this.country = country;
	}
	
	public int getID() { return id; }

	public String getStreetAddress() { return street_address; }
	public String getCity() { return city; }
	public String getState() { return state; }

	public String getPostalCode() { return postal_code; }
	public String getCountry() { return country; }

	public void setStreet_address(String street_address) {
		this.street_address = street_address;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setPostalCode(String postal_code) {
		this.postal_code = postal_code;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
}
