package edu.wit.cilfonej.database.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class Foster extends UserRole {

	@OneToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false)
	private Address home_address;
	
	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Address mailing_address;
	
	// no-args constructor
	Foster() {}
	
	public Foster(String firstname, String lastname, Address homeAddress) {
		this(new Person(firstname, lastname), homeAddress);
	}
	
	public Foster(Person person, Address homeAddress) {
		super(person);
		setHomeAddress(homeAddress);
	}

	public Address getHomeAddress() { return home_address; }
	public Address getMailingAddress() { return mailing_address; }

	public void setHomeAddress(Address address) {
		super.addAddress(address);
		this.home_address = address;
	}

	public void setMailingAddress(Address address) {
		super.addAddress(address);
		this.mailing_address = address;
	}
}
