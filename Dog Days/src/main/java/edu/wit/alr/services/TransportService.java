package edu.wit.alr.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.model.Dog;
import edu.wit.alr.database.model.TransportReservation;
import edu.wit.alr.database.model.roles.Caretaker;
import edu.wit.alr.database.repository.TransportReservationRepository;


@Service
public class TransportService {
	
	@Autowired
	private TransportReservationRepository repository;
	
	public TransportReservation createTransport(Address address, Dog dog, Caretaker person, LocalDate pickupDate) {
		TransportReservation transport = new TransportReservation(dog); 
		transport.setPickupAddress(address);
		transport.setPickupPerson(person);
		transport.setTransportDate(pickupDate);
		
		//TODO SEND EMAIL TO PEOPLE ABOPUT THIS
		repository.save(transport);
		return transport;
	}
	
	public TransportReservation findReservationForDog(Dog dog) {
		Iterator<TransportReservation> iter = repository.findAllByDog(dog).iterator();
		return iter.hasNext() ? iter.next() : null;
	}
	
	public Iterable<TransportReservation> getReservations(LocalDate date) {
		return repository.findAllByDate(date);
	}
	
	public LocalDate[] getClosestDateRange(LocalDate date) {
		Iterable<Date> dates = repository.findAllTransportDates();
		LocalDate prev = null;

		// set date to previous day, to mimic <= functionality
		date = date.minusDays(1);
		
		// go through all the transports, in increasing order
		for(Iterator<Date> iter = dates.iterator(); iter.hasNext();) {
			LocalDate transDate = iter.next().toLocalDate();
					
			// until a transport-date is after the date specified
			if(transDate.isAfter(date)) {
				return new LocalDate[] { prev, transDate, iter.hasNext() ? iter.next().toLocalDate() : null };
			}
			
			prev = transDate;
		}
		
		return new LocalDate[] { null, date, null };
	}
}
