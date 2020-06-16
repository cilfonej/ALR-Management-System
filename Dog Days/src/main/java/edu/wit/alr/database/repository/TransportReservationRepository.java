package edu.wit.alr.database.repository;

import org.springframework.data.repository.CrudRepository;

import edu.wit.alr.database.model.TransportReservation;

public interface TransportReservationRepository extends CrudRepository<TransportReservation, Integer> {

}
