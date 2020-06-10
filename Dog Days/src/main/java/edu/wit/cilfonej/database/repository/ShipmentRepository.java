package edu.wit.cilfonej.database.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import edu.wit.cilfonej.database.model.Dog;
import edu.wit.cilfonej.database.model.Shipment;

public interface ShipmentRepository extends CrudRepository<Shipment, Integer> {
	
	@Query("SELECT s FROM Shipment s WHERE s.for_dog = ?1")
	public Iterable<Shipment> findAllForDog(Dog dog);
}
