package edu.wit.alr.database.model.roles;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.model.Contact.PhoneContact;
import edu.wit.alr.database.model.Person;

@Entity
public abstract class Caretaker extends Role {
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn
	private Address home_address;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn
	private Address mailing_address;

	@Embedded
	private PhoneContact phone;
	
	// no-args constructor
	Caretaker() { }
		
	protected Caretaker(Person person) {
		this.person = person;
	}
	
	public PhoneContact getPrimaryPhone() { return phone; }
	
	public Address getHomeAddress() { return home_address; }
	public Address getMailingAddress() { return mailing_address; }
	
	public void setPrimaryPhone(PhoneContact phone) {
		this.phone = phone;
	}
	
	public void setHomeAddress(Address address) {
		this.home_address = address;
	}
	
	public void setMailingAddress(Address address) {
		this.mailing_address = address;
	}
}