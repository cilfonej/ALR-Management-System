package edu.wit.alr.database.model.roles;

import javax.persistence.Entity;

import edu.wit.alr.database.model.Person;

@Entity
public abstract class Caretaker extends Role {
	
	// no-args constructor
	Caretaker() { }
		
	protected Caretaker(Person person) {
		super(person);
	}
}