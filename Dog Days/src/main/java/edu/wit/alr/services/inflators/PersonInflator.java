package edu.wit.alr.services.inflators;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonAlias;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Admin;
import edu.wit.alr.database.model.roles.Adopter;
import edu.wit.alr.database.model.roles.ApplicationCoordinator;
import edu.wit.alr.database.model.roles.Caretaker;
import edu.wit.alr.database.model.roles.Foster;
import edu.wit.alr.database.model.roles.Role;
import edu.wit.alr.database.repository.PersonRepository;
import edu.wit.alr.services.PersonService;
import edu.wit.alr.services.inflators.AddressInflator.AddressData;
import edu.wit.alr.services.inflators.PersonInflator.PersonData;
import edu.wit.alr.web.lookups.PersonLookupOption;

@Component
public class PersonInflator implements Inflator<Person, PersonData> {
	
	@Autowired private PersonRepository repository;
	@Autowired private PersonService service;
	
	@Autowired private InflatorService inflator;
	
	public static class PersonData {
		@JsonAlias({"person_id", "personID"})
		public Integer id;
		
		@JsonAlias({"person_option", "personOption"})
		public PersonLookupOption option;
		
		public String name;
		
		public String email;
		public String phone;
		
		public String[] roles;	// only one of these fields will be populated
		public String role;		// this one is used when there was only one role option

		public AddressData address;
	}
	
	public Person inflate(PersonData data) {
		// TODO: allow of ID/Option and Role(s) to add new role to existing person
		
		if(data.option != null) {
			data.id = data.option.getID();
		}
		
		if(data.id != null) {
			Optional<Person> address = repository.findById(data.id);
			if(address.isPresent()) 
				return address.get();
			
			throw new InflationException("No person exists with provided 'id'");
		}
		
		if(data.name != null && data.email != null) {
			// get list of roles
			String[] roleNames = data.roles != null ? data.roles : new String[] { data.role };
			if(roleNames.length < 1)
				throw new InflationException("Cannot inflate Person! No 'role' provided");
			
			// split apart person's first and last name
			String[] name_parts = data.name.split(" ");
			if(name_parts.length != 2)
				throw new InflationException("Cannot inflate Person! Bad 'name' provided");
			
			Address address = data.address == null ? null : inflator.inflate(data.address);
			
			List<Class<? extends Role>> roles = service.toRoles(roleNames);
			
			// create objects
			try {
				return service.createPerson(name_parts[0].trim(), name_parts[1].trim(), data.email, data.phone, address, null, roles);
			} catch(RuntimeException e) {
				throw new InflationException("Cannot inflate Person!", e);
			}
		}
		
		throw new InflationException("Cannot inflate Person! No 'id' was provided");
	}
	
	@Transactional // NOTE: should be 'readonly' but due to inflate possibly needing to save to the Repo, it cannot
	public <T extends Role> T inflate(PersonData data, Class<T> role) {
		return inflate(data).findRole(role);
	}
}
