package edu.wit.alr.database.model.roles;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import edu.wit.alr.database.model.Person;

@Entity
@DiscriminatorValue("adopter")
public class Adopter extends Caretaker {
	
	
	// no-args constructor
	Adopter() { }
			
	public Adopter(Person person) {
		this.person = person;
	}
}
