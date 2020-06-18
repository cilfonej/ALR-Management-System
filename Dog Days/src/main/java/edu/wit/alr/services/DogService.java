package edu.wit.alr.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wit.alr.database.model.Dog;
import edu.wit.alr.database.model.Dog.Gender;
import edu.wit.alr.database.model.roles.ApplicationCoordinator;
import edu.wit.alr.database.repository.DogRepository;

@Service
public class DogService {
	
	@Autowired
	private DogRepository repository;
	
	public Dog createDog(String name, Gender gender, Double weight,
			Integer yearBorn, Integer monthBorn, Integer dayBorn, LocalDate heartDate, LocalDate ftDate,
			ApplicationCoordinator coordinator, String recruiter, String description) {
		
		// TODO: parameter validation
		// TODO: allow for images
		
		Dog dog = new Dog(name);
		
		dog.setGender(gender);
		
		if(weight != null) 
			dog.setWeight(weight);
		
		dog.setBirthday(yearBorn, monthBorn, dayBorn);
		
		dog.setAddoptionCoordinator(coordinator);
		dog.setDescription(description);
		
		repository.save(dog);
		
		// TODO: Email about new do in system
		
		return dog;
	}
}