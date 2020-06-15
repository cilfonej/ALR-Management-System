package edu.wit.alr.database.model.roles;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.model.Contact.PhoneContact;
import edu.wit.alr.database.model.Person;

@Entity
@DiscriminatorValue("admin")
public class Admin extends Role {
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn
	private Address mailing_address;
	
	@Embedded
	private PhoneContact phone;
	
	// no-args constructor
	Admin() { }
		
	protected Admin(Person person) {
		this.person = person;
	}
	
	public PhoneContact getPrimaryPhone() { return phone; }
	public Address getMailingAddress() { return mailing_address; }
	
	public void setPrimaryPhone(PhoneContact phone) {
		this.phone = phone;
	}
	
	public void setMailingAddress(Address address) {
		this.mailing_address = address;
	}
}
