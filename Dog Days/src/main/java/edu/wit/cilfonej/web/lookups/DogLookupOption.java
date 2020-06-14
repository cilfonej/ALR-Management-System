package edu.wit.cilfonej.web.lookups;

import edu.wit.cilfonej.database.model.Dog;

public class DogLookupOption extends LookupOption {

	protected String name;
	protected int number;
	
	public DogLookupOption(Dog dog) {
		super("dog", 
				dog.getName() + "#" + dog.getID(), 
				dog.getName() + "#" + dog.getID(),
				dog.getID());
		
		this.name = dog.getName();
		this.number = dog.getID();
	}
}
