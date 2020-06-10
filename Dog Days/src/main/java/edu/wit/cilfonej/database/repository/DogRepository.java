package edu.wit.cilfonej.database.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import edu.wit.cilfonej.database.model.Dog;
import edu.wit.cilfonej.database.model.Person;

public interface DogRepository extends CrudRepository<Dog, Integer> {

	@Query("SELECT d FROM Dog d WHERE d.custody = ?1")
	public Iterable<Dog> findAllByCustodian(Person person);

	@Query("SELECT d FROM Dog d WHERE d.name = ?1")
	public Optional<Dog> findByName(String name);
}
