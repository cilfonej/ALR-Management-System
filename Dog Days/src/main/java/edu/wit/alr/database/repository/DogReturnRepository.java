package edu.wit.alr.database.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import edu.wit.alr.database.model.Dog;
import edu.wit.alr.database.model.DogReturn;
import edu.wit.alr.database.model.Person;

public interface DogReturnRepository extends CrudRepository<DogReturn, Integer> {

	@Query("SELECT r FROM DogReturn r WHERE r.dog = ?1")
	public Optional<DogReturn> findByDog(Dog dog);
	
	@Query("SELECT d FROM DogReturn d INNER JOIN Role r ON r.id = d.addopter WHERE r.person = ?1")
	public Iterable<DogReturn> findAllByPerson(Person person);
}
