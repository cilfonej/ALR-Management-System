package edu.wit.alr.web.response;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.repository.AddressRepository;
import edu.wit.alr.database.repository.PersonRepository;

@Component
public class AddressInflator {
	
	@Autowired private PersonRepository personRepository;
	@Autowired private AddressRepository addressRepository;
	
	public static class AddressInfo {
		private Address address;
		private Person person;
		
		public AddressInfo(Address address, Person person) {
			this.address = address;
			this.person = person;
		}
		
		public Address getAddress() { return address; }
		public Person getPerson() { return person; }
	}
	
	@Transactional(readOnly = true)
	public AddressInfo inflate(AddressDetails details) {
		if(details.address_id > 0) {
			Optional<Address> address_opt = addressRepository.findById(details.address_id);
			if(address_opt.isEmpty()) {
				throw new InflationException("No such address with ID: " + details.address_id);
			}
			
			Address address = address_opt.get();
			return new AddressInfo(address, address.getPerson());
		}
		
		if(details.person_id > 0 && !StringUtils.isEmpty(details.streetName)) {
			Optional<Person> person_opt = personRepository.findById(details.person_id);
			if(person_opt.isEmpty()) {
				throw new InflationException("No such person with ID: " + details.person_id);
			}

			Address address = new Address(details.streetName, details.city, details.state, details.postalCode);
			Person person = person_opt.get();
			person.addAddress(address);
			
			personRepository.save(person);
			return new AddressInfo(address, person);
		}
		
		throw new InflationException("Must provide 'address_id' or, at least, 'person_id' && 'streetName'");
	}
}
