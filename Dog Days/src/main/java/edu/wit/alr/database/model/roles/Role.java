package edu.wit.alr.database.model.roles;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.model.Contact.EmailContact;
import edu.wit.alr.database.model.Contact.PhoneContact;
import edu.wit.alr.database.model.Person;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Role {
	
	@Id
	@GeneratedValue
	@Column(unique = true, nullable = false)
	private int id;
	
	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false) 
	protected Person person;
	
	// no-args constructor
	Role() { }
	
	protected Role(Person person) {
		this.person = person;
		person.addRole(this);
	}

	public String getName() { return person.getName(); }
	public String getFirstname() { return person.getFirstname(); }
	public String getLastname() { return person.getLastname(); }

	public Person getPerson() { return person; }

	public EmailContact getEmail() { return person.getEmail(); }
	public PhoneContact getPrimaryPhone() { return person.getPrimaryPhone(); }

	public Address getHomeAddress() { return person.getHomeAddress(); }
	public Address getMailingAddress() { return person.getMailingAddress(); }

	public void setEmail(EmailContact email) {
		person.setEmail(email);
	}

	public void setPrimaryPhone(PhoneContact phone) {
		person.setPrimaryPhone(phone);
	}

	public void setHomeAddress(Address address) {
		person.setHomeAddress(address);
	}

	public void setMailingAddress(Address address) {
		person.setMailingAddress(address);
	}
}
