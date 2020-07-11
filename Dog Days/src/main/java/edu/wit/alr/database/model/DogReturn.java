package edu.wit.alr.database.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import edu.wit.alr.database.model.roles.Adopter;

@Entity
public class DogReturn extends DBObject {
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private Dog dog;
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private Adopter addopter;
	
	@Lob
	@Column
	private String reason;
	
	@Column
	private LocalDate return_date;

	// no-args constructor
	DogReturn() { }
	
	public DogReturn(Dog dog, Adopter addopter, String reason) {
		this.dog = dog;
		this.addopter = addopter;
		
		this.reason = reason;
		this.return_date = LocalDate.now();
	}
	
	public Dog getDog() { return dog; }
	public Adopter getAddopter() { return addopter; }
	
	public String getReason() { return reason; }
	public LocalDate getReturnDate() { return return_date; }
	
	public void setReason(String reason) {
		this.reason  = reason;
	}
	
	public void setReturnDate(LocalDate date) {
		this.return_date = date;
	}
}
