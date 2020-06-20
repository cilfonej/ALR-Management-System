package edu.wit.alr.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wit.alr.database.model.Dog;
import edu.wit.alr.database.repository.DogRepository;

@Service	
public class DogService {
	
	@Autowired
	private DogRepository repository;
	
	public void updateInfo(Dog dog, double weight, LocalDate medDate, Integer birthMonth, Integer birthDay, Integer birthYear) {
		
		dog.setWeight(weight);
		//TODO make / set medication date in Dog.java
		//dog.setMedDate(medDate);
		dog.setBirthday(birthYear, birthMonth, birthDay);
		
		
		
		repository.save(dog);
		
	}
	
}
