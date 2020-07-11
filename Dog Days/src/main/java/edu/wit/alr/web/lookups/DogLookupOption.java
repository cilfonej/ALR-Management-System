package edu.wit.alr.web.lookups;

import edu.wit.alr.database.model.Dog;

public class DogLookupOption extends LookupOption {

	protected String name;
	protected int number;
	
	// no-args constructor
	DogLookupOption() { }
	
	public DogLookupOption(Dog dog) {
		super("dog", 
				dog.getName() + "#" + dog.getId(), 
				dog.getName() + "#" + dog.getId(),
				dog.getId());
		
		this.name = dog.getName();
		this.number = dog.getId();
	}
	
	public int getID() { return number; }
}
