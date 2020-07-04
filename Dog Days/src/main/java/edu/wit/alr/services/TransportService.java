package edu.wit.alr.services;

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
	
	public void createTransport(Address address, Dog dog, Caretaker person, LocalDate pickupDate) {
		TransportReservation transport = new TransportReservation(dog); 
		transport.setPickupAddress(address);
		transport.setPickupPerson(person);
		transport.setTransportDate(pickupDate);
		
		//TODO SEND EMAIL TO PEOPLE ABOPUT THIS
		repository.save(transport);
	}
	
	public TransportReservation findReservationForDog(Dog dog) {
		Iterator<TransportReservation> iter = repository.findAllByDog(dog).iterator();
		return iter.hasNext() ? iter.next() : null;
	}
}
