package edu.wit.alr.services.inflators;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonAlias;

import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Role;
import edu.wit.alr.database.repository.PersonRepository;
import edu.wit.alr.services.inflators.PersonInflator.PersonData;
import edu.wit.alr.web.lookups.PersonLookupOption;

@Component
public class PersonInflator implements Inflator<Person, PersonData> {
	
	@Autowired
	private PersonRepository repository;
	
	public static class PersonData {
		@JsonAlias({"person_id", "personID"})
		public Integer id;
		
		@JsonAlias({"person_option", "personOption"})
		public PersonLookupOption option;
	}
	
	public Person inflate(PersonData data) {
		if(data.option != null) {
			data.id = data.option.getID();
		}
		
		if(data.id != null) {
			Optional<Person> address = repository.findById(data.id);
			if(address.isPresent()) 
				return address.get();
			
			throw new InflationException("No person exists with provided 'id'");
		}
		
		throw new InflationException("Cannot inflate Person! No 'id' was provided");
	}
	
	@Transactional(readOnly = true)
	public <T extends Role> T inflate(PersonData data, Class<T> role) {
		return inflate(data).findRole(role);
	}
}
