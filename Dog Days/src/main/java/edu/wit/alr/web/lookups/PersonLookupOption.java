package edu.wit.alr.web.lookups;

import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Admin;
import edu.wit.alr.database.model.roles.Adopter;
import edu.wit.alr.database.model.roles.ApplicationCoordinator;
import edu.wit.alr.database.model.roles.Caretaker;
import edu.wit.alr.database.model.roles.Foster;

public class PersonLookupOption extends LookupOption {
	protected String name;
	protected int id;
	
	// no-args constructor
	PersonLookupOption() { }
	
	public PersonLookupOption(Person person) {
		this("user", person);
	}
	
	private PersonLookupOption(String type, Person person) {
		super(type, person.getName(), person.getName(), person.getId());
		
		this.name = person.getName();
		this.id = person.getId();
	}
	
	public int getID() { return id; }
	
	public static class CaretakerLookupOption extends PersonLookupOption {
		public CaretakerLookupOption(Caretaker foster) {
			super("caretaker", foster.getPerson());
		}
	}
	
	public static class FostertLookupOption extends PersonLookupOption {
		public FostertLookupOption(Foster foster) {
			super("foster", foster.getPerson());
		}
	}
	
	public static class AdopterLookupOption extends PersonLookupOption {
		public AdopterLookupOption(Adopter foster) {
			super("adopter", foster.getPerson());
		}
	}
	
	public static class AdminLookupOption extends PersonLookupOption {
		public AdminLookupOption(Admin foster) {
			super("admin", foster.getPerson());
		}
	}
	
	public static class ApplicationCoordinatorLookupOption extends PersonLookupOption {
		public ApplicationCoordinatorLookupOption(ApplicationCoordinator foster) {
			super("coordinator", foster.getPerson());
		}
	}
}
