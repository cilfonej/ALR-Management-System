package edu.wit.alr.services.inflators;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAlias;

import edu.wit.alr.database.model.Dog;
import edu.wit.alr.database.repository.DogRepository;
import edu.wit.alr.services.inflators.DogInflator.DogData;
import edu.wit.alr.web.lookups.DogLookupOption;

@Component
public class DogInflator implements Inflator<Dog, DogData> {
	
	@Autowired
	private DogRepository repository;
	
	public static class DogData {
		@JsonAlias({"dog_id", "dogID"})
		public Integer id;
		
		@JsonAlias({"dog_option", "dogOption"})
		public DogLookupOption option;
	}
	
	public Dog inflate(DogData data) {
		if(data.option != null) {
			data.id = data.option.getID();
		}
		
		if(data.id != null) {
			Optional<Dog> address = repository.findById(data.id);
			if(address.isPresent()) 
				return address.get();
			
			throw new InflationException("No dog exists with provided 'id'");
		}
		
		throw new InflationException("Cannot inflate Dog! No 'id' was provided");
	}
}
