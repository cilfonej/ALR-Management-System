package edu.wit.alr.services.session;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import edu.wit.alr.database.model.Dog;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.repository.DogRepository;
import edu.wit.alr.database.repository.PersonRepository;

@Component
public class UserSessionData {
	public static enum SessionState {
		Unbound, Running, Ended
	};
	
	@Autowired private PersonRepository personRepository;
	@Autowired private DogRepository dogRepository;
	
	@Autowired
	private ProxyData2 data;
	
	public void bindUser(Person person) {
		if(data.state != SessionState.Unbound) {
			throw new IllegalStateException("Session has already been started!");
		}

		data.user_id = person.getID();
		data.user = person;
		
		data.state = SessionState.Running;
	}
	
	public void setDog(Dog dog) {
		data.dog_id = dog.getID();
		data.dog = dog;
	} 
	
//	======================================== =========== ======================================== \\
//	======================================== Deserialize ======================================== \\
	
	@Transactional(readOnly = true)
	private void reloadUser() {
		// if there was ever a user assigned
		if(data.user_id > 0) {
			Optional<Person> user = personRepository.findById(data.user_id);
			if(user.isEmpty()) {
				// mark as invalid
				data.user_id = -data.user_id;
			} else {
				data.user = user.get();
			}
		}
	}
	
	@Transactional(readOnly = true)
	private void reloadDog() {
		// if there was ever a dog assigned
		if(data.dog_id > 0) {
			Optional<Dog> dog = dogRepository.findById(data.dog_id);
			if(dog.isEmpty()) {
				// mark as invalid
				data.dog_id = -data.dog_id;
			} else {
				data.dog = dog.get();
			}
		}
	}

//	======================================== ============== ======================================== \\
//	======================================== Data Assessors ======================================== \\
	
	public Person getUser() { 
		if(data.user == null) reloadUser();
		return data.user; 
	}
	
	public Dog getDog() { 
		if(data.dog == null) reloadDog();
		return data.dog; 
	}

	public SessionState getState() { 
		if(data.state == null) data.state = SessionState.Unbound;
		return data.state; 
	}
	
//	======================================== ================== ======================================== \\
//	======================================== Proxy Session Data ======================================== \\
	
	@Component
	@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
	static class ProxyData2 implements Serializable {
		private static final long serialVersionUID = 4665901506808150577L;

		private SessionState state;
		
		private int user_id;
		private transient Person user;

		private int dog_id;
		private transient Dog dog;
	}
}
