package edu.wit.alr.database.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import edu.wit.alr.database.model.Person;

public interface PersonRepository extends CrudRepository<Person, Integer> {

	@Query("SELECT p FROM Person p WHERE p.firstname = ?1 AND p.lastname = ?2")
	public Optional<Person> findByName(String firstname, String lastname);
	
	@Query(nativeQuery = true, value = "SELECT * FROM Person p WHERE p.id LIKE ?1 OR p.firstname LIKE ?1 OR p.lastname LIKE ?1"
											+ " OR CONCAT(p.firstname, ' ', p.lastname) LIKE ?1")
	public Iterable<Person> findAllByFuzzyNameOrId(String search);
}
