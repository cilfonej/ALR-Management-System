package edu.wit.alr.database.model;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import edu.wit.alr.database.model.roles.Caretaker;

@Entity
public class TransportReservation extends DBObject {
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private Dog dog;
	
	@Column
	private LocalDate transportDate;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn
	private Address pickupAddress;
	
	@ManyToOne
	@JoinColumn
	private Caretaker pickupPerson;
	
	// no-args constructor
	TransportReservation() { }

	public TransportReservation(Dog dog) {
		this.dog = dog;
	}
	
	public Dog getDog() { return dog; }
	public Caretaker getPickupPerson() { return pickupPerson; }
	
	public LocalDate getTransportDate() { return transportDate; }
	public Address getPickupAddress() { return pickupAddress; }
	
	public void setTransportDate(LocalDate transportDate) {
		this.transportDate = transportDate;
	}

	public void setPickupAddress(Address pickupAddress) {
		this.pickupAddress = pickupAddress;
	}

	public void setPickupPerson(Caretaker pickupPerson) {
		this.pickupPerson = pickupPerson;
	}
}
