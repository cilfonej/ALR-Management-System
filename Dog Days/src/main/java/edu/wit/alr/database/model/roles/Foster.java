package edu.wit.alr.database.model.roles;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import edu.wit.alr.database.model.Person;

@Entity
@DiscriminatorValue("foster")
public class Foster extends Caretaker {
	
	
	// no-args constructor
	Foster() { }
			
	public Foster(Person person) {
		super(person);
	}
	
	public String toString() {
		return "Foster";
	}
}
