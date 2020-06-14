package edu.wit.alr.web.lookups;

import edu.wit.alr.database.model.Distributor;
import edu.wit.alr.database.model.Foster;
import edu.wit.alr.database.model.Person;

public class UserLookupOption extends LookupOption {
	protected String name;
	
	public UserLookupOption(Person person) {
		this("user", person);
	}
	
	private UserLookupOption(String type, Person person) {
		super(type, person.getName(), person.getName(), person.getID());
		
		this.name = person.getName();
	}
	
	public static class FostertLookupOption extends UserLookupOption {
		public FostertLookupOption(Foster foster) {
			super("foster", foster.getBasePerson());
		}
	}
	
	public static class DistributorLookupOption extends UserLookupOption {
		public DistributorLookupOption(Distributor distributor) {
			super("distributor", distributor.getBasePerson());
		}
	}
}
