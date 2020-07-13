package edu.wit.alr.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.InstantiationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.model.Contact;
import edu.wit.alr.database.model.Contact.EmailContact;
import edu.wit.alr.database.model.Contact.PhoneContact;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Admin;
import edu.wit.alr.database.model.roles.Adopter;
import edu.wit.alr.database.model.roles.ApplicationCoordinator;
import edu.wit.alr.database.model.roles.Caretaker;
import edu.wit.alr.database.model.roles.Foster;
import edu.wit.alr.database.model.roles.Role;
import edu.wit.alr.database.repository.PersonRepository;

@Service
public class PersonService {
	
	@Autowired
	private PersonRepository repository;
	
	public Person createPerson(String firstname, String lastname, String email, String phone, 
								Address home, Address mailing, Collection<Class<? extends Role>> roles) {
		
		Person person = new Person(firstname, lastname);
		
		// setup each role
		for(Class<? extends Role> clazz : roles) {
			// check for abstract role types
			if(clazz == Caretaker.class)
				throw new InstantiationException("Cannot create instance of Role", Caretaker.class);
			if(clazz == Admin.class)
				throw new InstantiationException("Cannot create instance of Role", Admin.class);

			/*	
			 * 	Role setup process is designed top-down. Each parent type checks its required field
			 * 	before allowing the creation (and automatic addition to person) of a Role. Once fields
			 * 	are validated, they are set by the defining level.
			 */
			
			Role role = null;
			
			// check for fields on Role.class
			if(email == null && !email.isEmpty()) 
				throw new NullPointerException("No 'email' for Role [adopter]");
			
			// check if the role is a Caretaker
			if(Caretaker.class.isAssignableFrom(clazz)) {
				Caretaker caretaker = null;

				// check for fields on Caretaker.class
				if(phone == null || phone.isEmpty()) throw new NullPointerException("No 'phone' for Role [adopter]");
				if(home == null) throw new NullPointerException("No 'home-address' for Role [adopter]");
				
				// construct role object
				if(clazz == Adopter.class) 		role = caretaker = new Adopter(person);
				else if(clazz == Foster.class) 	role = caretaker = new Foster(person);
				
				// set fields related to Caretaker
				caretaker.setPrimaryPhone(new PhoneContact(phone));
				caretaker.setHomeAddress(home);

			// check if the role is a Admin
			} else if(Admin.class.isAssignableFrom(clazz)) {
				Admin admin = null;

				// check for fields on Admin.class
				
				// construct role object
				if(clazz == ApplicationCoordinator.class) 
					role = admin = new ApplicationCoordinator(person);

				// set fields related to Admin
				
				if(phone != null && !phone.isEmpty())
					admin.setPrimaryPhone(new PhoneContact(phone));
				
				// pick the most appropriate address to use
				if(home != null || mailing != null)
					admin.setMailingAddress(mailing == null ? home : mailing);
			}
			
			// set fields related to Role
			
			role.setEmail(new EmailContact(email));
		}
		
		// insert person into repository
		repository.save(person);
		return null;
	}
	
	public List<Class<? extends Role>> toRoles(String[] roleNames) {
		List<Class<? extends Role>> roles = new ArrayList<>();
		for(String role : roleNames) {
			switch(role.toLowerCase()) {
				case "caretaker": roles.add(Caretaker.class); break;
				case "adopter": roles.add(Adopter.class); break;
				case "foster": roles.add(Foster.class); break;

				case "admin": roles.add(Admin.class); break;
				case "coordinator": roles.add(ApplicationCoordinator.class); break;
			}
		}
		
		return roles;
	}
	
	//TODO person user needs to be account later
	public Person updateUserInfo(Person user, String firstName, String lastName, String email, String phone, Address homeAddress, Address mailAddress) {
		user.setFirstname(firstName);
		user.setLastname(lastName);
		user.setEmail(new EmailContact(email));
		user.setPrimaryPhone(new PhoneContact(phone));
		user.setHomeAddress(homeAddress);
		user.setMailingAddress(mailAddress);
		repository.save(user);
		return user;
	}
}
