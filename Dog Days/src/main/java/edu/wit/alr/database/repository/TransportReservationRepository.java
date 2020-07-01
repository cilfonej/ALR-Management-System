package edu.wit.alr.database.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import edu.wit.alr.database.model.Dog;
import edu.wit.alr.database.model.TransportReservation;

public interface TransportReservationRepository extends CrudRepository<TransportReservation, Integer> {

	@Query("SELECT r FROM TransportReservation r WHERE r.dog = ?1 ORDER BY r.transportDate DESC")
	public Optional<TransportReservation> findByDog(Dog dog);
}
