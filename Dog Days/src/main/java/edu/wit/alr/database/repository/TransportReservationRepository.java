package edu.wit.alr.database.repository;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import edu.wit.alr.database.model.TransportReservation;

public interface TransportReservationRepository extends CrudRepository<TransportReservation, Integer> {
	@Query("SELECT t FROM TransportReservation t WHERE t.transportDate = ?1")
	public Iterable<TransportReservation> findAllByDate(LocalDate date);
	
	@Query(nativeQuery = true, value = "SELECT t.transportDate FROM TransportReservation t "
									 + "GROUP BY t.transportDate ORDER BY t.transportDate ASC")
	public Iterable<Date> findAllTransportDates();
}