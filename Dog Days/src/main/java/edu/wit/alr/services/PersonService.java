package edu.wit.alr.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.InstantiationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.wit.alr.database.model.Address;
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
			constructRole(person, clazz, email, phone, home, mailing);
		}
		
		// insert person into repository
		repository.save(person);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Role> T constructRole(Person person, Class<T> type, String email, String phone, Address home, Address mailing) {
		// check for abstract role types
		if(type == Caretaker.class)
			throw new InstantiationException("Cannot create instance of Role", Caretaker.class);
		if(type == Admin.class)
			throw new InstantiationException("Cannot create instance of Role", Admin.class);

		Role role;
		
		// attempt to build roleby type
		if(type == Adopter.class) role = new Adopter(person);
		else if(type == Foster.class) role = new Foster(person);
		else if(type == ApplicationCoordinator.class) role = new ApplicationCoordinator(person);
		else throw new IllegalArgumentException("Unknown Type: " + type.getSimpleName());

		// perform setup process
		return (T) updateRole(role, email, phone, home, mailing);
	}
	
	public <T extends Role> T updateRole(T role, String email, String phone, Address home, Address mailing) {
		/*	
		 * 	Role setup process is designed top-down. Each parent type checks its required field
		 * 	before allowing the creation (and automatic addition to person) of a Role. Once fields
		 * 	are validated, they are set by the defining level.
		 */
		
		// check for fields on Role.class
		if(email == null || email.isEmpty()) 
			throw new NullPointerException("No 'email' for Role");
		
		// check if the role is a Caretaker
		if(role instanceof Caretaker) {
			Caretaker caretaker = (Caretaker) role;

			// check for fields on Caretaker.class
			if(phone == null || phone.isEmpty()) throw new NullPointerException("No 'phone' for Role [Caretaker]");
			if(home == null) throw new NullPointerException("No 'home-address' for Role [Caretaker]");
			
			// set fields related to Caretaker
			
			caretaker.setPrimaryPhone(new PhoneContact(phone));
			caretaker.setHomeAddress(home);

		// check if the role is a Admin
		} else if(role instanceof Admin) {
			Admin admin = (Admin) role;

			// check for fields on Admin.class
			// <none>
			
			// set fields related to Admin
			
			if(phone != null && !phone.isEmpty())
				admin.setPrimaryPhone(new PhoneContact(phone));
			
			// pick the most appropriate address to use
			if(home != null || mailing != null)
				admin.setMailingAddress(mailing == null ? home : mailing);
		}
		
		// set fields related to Role
		
		role.setEmail(new EmailContact(email));
		return role;
	}
	
	public Person updatePersonDetails(Person person, String firstname, String lastname) {
		person.setFirstname(firstname);
		person.setLastname(lastname);
		
		repository.save(person);
		return person;
	}
	
	@Transactional
	public <T extends Role> T updatePersonRole(Person person, Class<T> type, String email, String phone, Address home, Address mail) {
		T role = person.findRole(type);
		
		if(role == null)
			role = constructRole(person, type, email, phone, home, mail);
		else
			role = updateRole(role, email, phone, home, mail);
		
		repository.save(person);
		return role;
	}
	
	public List<Class<? extends Role>> toRoles(String... roleNames) {
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
	
	@Transactional(readOnly = true)
	public Person findPersonByID(int id) {
		Person person = repository.findById(id).orElse(null);
		if(person == null) return null;
		
		// force lazy-initialization of Person's roles
		person.findRole(Role.class);
		return person;
	}
}
