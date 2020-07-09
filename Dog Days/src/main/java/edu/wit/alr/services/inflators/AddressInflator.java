package edu.wit.alr.services.inflators;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAlias;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.repository.AddressRepository;
import edu.wit.alr.services.inflators.AddressInflator.AddressData;
import edu.wit.alr.web.lookups.AddressLookupOption;

@Component
public class AddressInflator implements Inflator<Address, AddressData> {
	
	@Autowired
	private AddressRepository repository;
	
	public static class AddressData {
		@JsonAlias({"street"})
		public String streetName;
	
		public String city;
		public String state;

		public String zipcode;
		public String country;
	
		@JsonAlias({"address_id", "addressID"})
		public Integer id;
		@JsonAlias({"address_option", "addressOption"})
		public AddressLookupOption option;
	}
	
	public Address inflate(AddressData data) {
		if(data.option != null) {
			data.id = data.option.getID();
		}
		
		if(data.id != null) {
			Optional<Address> address = repository.findById(data.id);
			if(address.isPresent()) 
				return address.get();
			
			throw new InflationException("No address exists with provided 'id'");
		}
		
		if(data.streetName != null) {
			data.streetName = data.streetName.trim();
			
			if(data.city != null) data.city = data.city.trim();
			if(data.state != null) data.state = data.state.trim();
			if(data.zipcode != null) data.zipcode = data.zipcode.trim();
			if(data.country != null) data.country = data.country.trim();
			
			// force there to be a streetName, city, and valid state
			if(data.streetName.isBlank() || data.city == null || data.city.isBlank() || data.state == null || data.state.isBlank()) {
				throw new InflationException("Cannot inflate Address! Not enough address-information was provided");
			}

			// if the fields were going to be empty, make it null
			if(data.zipcode.isBlank()) data.zipcode = null;
			if(data.country.isBlank()) data.country = null;
			
			// construct new address 
			return new Address(data.streetName, data.city, data.state, data.zipcode, data.country);
		}
		
		throw new InflationException("Cannot inflate Address! No 'streetName' or 'id' was provided");
	}
}
