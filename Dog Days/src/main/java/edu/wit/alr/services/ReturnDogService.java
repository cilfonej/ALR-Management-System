package edu.wit.alr.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wit.alr.database.model.Dog;
import edu.wit.alr.database.model.DogReturn;
import edu.wit.alr.database.model.roles.Adopter;
import edu.wit.alr.database.model.roles.Caretaker;
import edu.wit.alr.database.repository.DogReturnRepository;

@Service
public class ReturnDogService {
	
	@Autowired
	private DogReturnRepository repository;
	
	@Autowired
	private DogService dogService;
	
	public void createReturnDog(Dog dog, Adopter returnPerson, Caretaker newPerson, String reason, LocalDate returnDate) {
		DogReturn returning = new DogReturn(dog, returnPerson, reason); 
		dogService.updateCaretaker(dog, newPerson);	
		returning.setReason(reason);
		returning.setReturnDate(returnDate);
		
		repository.save(returning);	
	}	
	
	public DogReturn findReturnByID(int id) {
		return repository.findById(id).orElse(null);
	}
	
	public DogReturn updateReturn(DogReturn dogReturn, LocalDate returnDate, String reason) {
		dogReturn.setReturnDate(returnDate);
		dogReturn.setReason(reason);
		repository.save(dogReturn);	
		return dogReturn;
	}
}