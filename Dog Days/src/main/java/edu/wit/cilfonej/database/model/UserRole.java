package edu.wit.cilfonej.database.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class UserRole {
	
	@Id
	@GeneratedValue
	@Column(unique = true, nullable = false)
	private int id;
	
	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false) 
	protected Person person;
	
	// no-args constructor
	UserRole() { }
	
	protected UserRole(Person person) {
		this.person = person;
	}

	public String getName() { return person.getName(); }
	public String getFirstname() { return person.getFirstname(); }
	public String getLastname() { return person.getLastname(); }

	public Contact getPrimaryContact() { return person.getPrimaryContact(); }
	public Set<Contact> getContacts() { return person.getContacts(); }
	public Set<Address> getAddresses() { return person.getAddresses(); }

	public Person getBasePerson() { return person; }
	
	public void setFirstname(String firstname) {
		person.setFirstname(firstname);
	}

	public void setLastname(String lastname) {
		person.setLastname(lastname);
	}

	public void setPrimaryContact(Contact contact) {
		person.setPrimaryContact(contact);
	}

	public void addContact(Contact contact) {
		person.addContact(contact);
	}

	public boolean removeContact(Contact contact) {
		return person.removeContact(contact);
	}

	public void addAddress(Address address) {
		person.addAddress(address);
	}

	public boolean removeAddress(Address address) {
		return person.removeAddress(address);
	}
}
