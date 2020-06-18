package edu.wit.alr.services.inflators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.model.Dog;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Role;
import edu.wit.alr.services.inflators.AddressInflator.AddressData;
import edu.wit.alr.services.inflators.DogInflator.DogData;
import edu.wit.alr.services.inflators.PersonInflator.PersonData;

@Service
public class InflatorService {
	
	@Autowired private AddressInflator addressInflator;
	@Autowired private DogInflator dogInflator;
	@Autowired private PersonInflator personInflator;
	
	public Address inflateAddress(AddressData data) {
		return addressInflator.inflate(data);
	}
	
	public Dog inflateDog(DogData data) {
		return dogInflator.inflate(data);
	}
	
	public Person inflatePerson(PersonData data) {
		return personInflator.inflate(data);
	}
	
	public <T extends Role> T inflatePerson(PersonData data, Class<T> role) {
		return personInflator.inflate(data, role);
	}

// ===================================== Generic 'inflate' Method ===================================== \\
	
	public Address inflate(AddressData data) { return inflateAddress(data); }
	public Dog inflate(DogData data) { return inflateDog(data); }
	public Person inflate(PersonData data) { return inflatePerson(data); }
}
