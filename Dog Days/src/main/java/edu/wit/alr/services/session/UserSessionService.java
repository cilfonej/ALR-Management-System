package edu.wit.alr.services.session;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.repository.PersonRepository;

@Service
public class UserSessionService {
	
	@Autowired private PersonRepository personRepository;
	
	@Autowired
	private UserSessionData data;
	
	public UserSessionData resumeSession() {
		switch(data.getState()) {
			case Unbound:
				startSession();
			
			case Running:
				return data;
			
			default:
			case Ended:
				throw new IllegalStateException("This session has already ended");
		}
	}

	@Transactional(readOnly = true)
	private void startSession() {
		Optional<Person> person = personRepository.findByName("Ann", "Cilfone");
		if(person.isEmpty()) 
			throw new NoSuchElementException("Person could not be found!");
		
		startSession(person.get());
	}
	
	private void startSession(Person person) {
		data.bindUser(person);
	}
}
