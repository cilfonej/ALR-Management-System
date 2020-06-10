package edu.wit.cilfonej.services;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.wit.cilfonej.database.model.Dog;
import edu.wit.cilfonej.database.model.Foster;
import edu.wit.cilfonej.database.model.Person;
import edu.wit.cilfonej.database.repository.DogRepository;
import edu.wit.cilfonej.services.session.UserSessionData;
import edu.wit.cilfonej.services.session.UserSessionService;

@Service
public class DogDetailsService {
	@Autowired private DogRepository dogRepository;
	
	@Autowired private UserSessionService service;

	@Transactional(readOnly = true)
	public Dog initalizeDogInfo() {
		UserSessionData details = service.resumeSession();
		
		Dog dog = details.getDog();
		if(dog == null) {
			assignDog(details);
			dog = details.getDog();
		}
		
		Person custodian = dog.getCustodian();
		// ensure the role-data is loaded, if present 
		if(custodian != null)
			custodian.findRole(Foster.class);
		
		return dog;
	}
	
	@Transactional(readOnly = true)
	private void assignDog(UserSessionData details) {
		Iterator<Dog> dogs = dogRepository.findAll().iterator();
		if(!dogs.hasNext()) throw new NoSuchElementException("No dogs were found in the system");
		
		details.setDog(dogs.next());
	}

	@Transactional(readOnly = true)
	public Dog getDogOrCurrent(int dog_id) {
		if(dog_id > 0) {
			Optional<Dog> dogResult = dogRepository.findById(dog_id);
			
			if(dogResult.isPresent()) {
				Dog dog = dogResult.get();
				Person custodian = dog.getCustodian();
				
				if(custodian != null)
					custodian.findRole(Foster.class);
				return dog;
			}
		}
		
		return initalizeDogInfo();
	}
}
