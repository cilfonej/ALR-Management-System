package edu.wit.alr.database.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import edu.wit.alr.database.model.Contact.EmailContact;
import edu.wit.alr.database.model.Contact.PhoneContact;
import edu.wit.alr.database.model.roles.Role;

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
	
	@Embedded
	private EmailContact email;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn
	private Address home_address;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn
	private Address mailing_address;
	
	@Embedded
	private PhoneContact phone;
	
	@OneToMany(cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, mappedBy = "person", orphanRemoval = true)
	private Set<Role> roles;
	
	// no-args constructor
	Person() {
		roles = new HashSet<>();
	}

	public Person(String firstname, String lastname) {
		this();
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public int getID() { return id; }
	
	public String getName() { return firstname + " " + lastname; }
	public String getFirstname() { return firstname; }
	public String getLastname() { return lastname; }

	public EmailContact getEmail() { return email; }
	public PhoneContact getPrimaryPhone() { return phone; }
	
	public Address getHomeAddress() { return home_address; }
	public Address getMailingAddress() { return mailing_address; }
	
	public Set<Role> getRoles() { return roles; }

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public void setEmail(EmailContact email) {
		this.email = email;
	}
	
	public void setPrimaryPhone(PhoneContact phone) {
		this.phone = phone;
	}
	
	public void setHomeAddress(Address address) {
		this.home_address = address;
	}
	
	public void setMailingAddress(Address address) {
		this.mailing_address = address;
	}
	
	public void addRole(Role role) {
		this.roles.add(role);
	}

	@SuppressWarnings("unchecked")
	public <T extends Role> T findRole(Class<T> clazz) {
		for(Role role : roles) {
			if(clazz.isInstance(role)) {
				return (T) role;
			}
		}
		
		return null;
	}
}
