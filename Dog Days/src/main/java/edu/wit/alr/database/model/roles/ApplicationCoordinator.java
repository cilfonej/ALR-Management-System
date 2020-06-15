package edu.wit.alr.database.model.roles;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import edu.wit.alr.database.model.Person;

@Entity
@DiscriminatorValue("coordinator")
public class ApplicationCoordinator extends Admin {
	
	
	// no-args constructor
	ApplicationCoordinator() { }
			
	public ApplicationCoordinator(Person person) {
		this.person = person;
	}
}
