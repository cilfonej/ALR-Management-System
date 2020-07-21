package edu.wit.alr.database.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import edu.wit.alr.database.model.Dog;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Caretaker;

public interface DogRepository extends CrudRepository<Dog, Integer> {

	@Query("SELECT d FROM Dog d WHERE d.caretaker = ?1")
	public Iterable<Dog> findAllByCustodian(Caretaker caretaker);

	@Query("SELECT d FROM Dog d WHERE d.name = ?1")
	public Optional<Dog> findByName(String name);

	@Query(nativeQuery = true, value = "SELECT * FROM Dog d WHERE d.id LIKE ?1 OR d.name LIKE ?1")
	public Iterable<Dog> findAllByFuzzyNameOrId(String search);
	
	@Query(nativeQuery = true, value = "SELECT * FROM dog d INNER JOIN role r ON d.caretaker_id = r.id WHERE r.DTYPE = \"adopter\" AND r.person_id = ?1")
	public Iterable<Dog> findAllByAdopter(Person adopter);
	
	@Query(nativeQuery = true, value = "SELECT * FROM dog d INNER JOIN role r ON d.caretaker_id = r.id WHERE r.DTYPE = \"foster\" AND r.person_id = ?1")
	public Iterable<Dog> findAllByFoster(Person foster);
	
	@Query(nativeQuery = true, value = "SELECT * FROM dog d INNER JOIN role R ON d.coordinator_id = r.id WHERE r.person_id = ?1")
	public Iterable<Dog> findAllByCoordinator(Person coordinator);
}
