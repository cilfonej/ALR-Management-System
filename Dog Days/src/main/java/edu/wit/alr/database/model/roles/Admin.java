package edu.wit.alr.database.model.roles;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import edu.wit.alr.database.model.Person;

@Entity
@DiscriminatorValue("admin")
public class Admin extends Role {
	
	// no-args constructor
	Admin() { }
		
	protected Admin(Person person) {
		super(person);
	}
}
