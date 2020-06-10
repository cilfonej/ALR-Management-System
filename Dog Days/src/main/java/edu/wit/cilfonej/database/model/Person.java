package edu.wit.cilfonej.database.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;

@Entity
public class Person {

	@Id
	@GeneratedValue
	@Column(unique = true, nullable = false)
	private int id;

	@Column(length = 25, nullable = false)
	private String firstname;

	@Column(length = 25, nullable = false)
	private String lastname;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	private Contact primary_contact;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "contact_for")
	private Set<Contact> contacts;
	
	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "address_for")
	private Set<Address> addresses;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "person")
	private Set<UserRole> roles;
	
	// no-args constructor
	Person() {
		contacts = new HashSet<>();
		addresses = new HashSet<>();
	}

	public Person(String firstname, String lastname) {
		this();
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public Person(Person person) {
		this();
		
		// if person has be entered
		if(person.id > 0) {
			this.id = person.id;
		
		// if person is still transient, clones values
		} else {
			this.firstname = person.firstname;
			this.lastname = person.lastname;
			
			this.contacts = person.contacts;
			this.primary_contact = person.primary_contact;
		}
	}

	public int getID() { return id; }
	
	public String getName() { return firstname + " " + lastname; }
	public String getFirstname() { return firstname; }
	public String getLastname() { return lastname; }

	public Contact getPrimaryContact() { return primary_contact; }
	public Set<Contact> getContacts() { return Collections.unmodifiableSet(contacts); }
	public Set<Address> getAddresses() { return Collections.unmodifiableSet(addresses); }
	
	public Set<UserRole> getRoles() { return roles; }

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public void setPrimaryContact(Contact contact) {
		this.primary_contact = contact;
		this.addContact(contact);
	}
	
	public void addContact(Contact contact) {
		if(contact.getPerson() == null) {
			contact.setPerson(this);
			
		} else if(!this.equals(contact.getPerson()))
			throw new IllegalArgumentException("Provided Contact is already for someone else");
		
		if(this.primary_contact == null)
			setPrimaryContact(contact);
		
		contacts.add(contact);
	}
	
	public boolean removeContact(Contact contact) {
		if(this.primary_contact != null && this.primary_contact.equals(contact))
			throw new IllegalStateException("Cannot remove primary-contact");
		
		return contacts.remove(contact);
	}
	
	public void addAddress(Address address) {
		if(address.getPerson() == null) {
			address.setPerson(this);
			
		} else if(!this.equals(address.getPerson()))
			throw new IllegalArgumentException("Provided Address is already for someone else");
		
		addresses.add(address);
	}
	
	public boolean removeAddress(Address address) {
		address.setPerson(null);
		return addresses.remove(address);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends UserRole> T findRole(Class<T> clazz) {
		for(UserRole role : roles) {
			if(clazz.isInstance(role)) {
				return (T) role;
			}
		}
		
		return null;
	}
	
	@PreRemove
	protected void beforeRemove() {
		// detach any addresses before delete
		addresses.forEach(address -> address.setPerson(null));
	}
}
