package edu.wit.alr.web.controllers.forms;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.model.Dog;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Adopter;
import edu.wit.alr.database.model.roles.Caretaker;
import edu.wit.alr.database.model.roles.Foster;
import edu.wit.alr.database.repository.DogRepository;
import edu.wit.alr.database.repository.PersonRepository;
import edu.wit.alr.services.DogService;
import edu.wit.alr.services.TransportService;
import edu.wit.alr.services.inflators.AddressInflator.AddressData;
import edu.wit.alr.services.inflators.DogInflator.DogData;
import edu.wit.alr.services.inflators.InflatorService;
import edu.wit.alr.services.inflators.PersonInflator.PersonData;
import edu.wit.alr.web.lookups.DogLookupOption;
import edu.wit.alr.web.lookups.LookupOption.LookupGroup;
import edu.wit.alr.web.lookups.PersonLookupOption;
import edu.wit.alr.web.response.PageResponse;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller
@RequestMapping("/register/transport")
public class TransportController {
	
	@Autowired
	private InflatorService inflatorService;
	
	@Autowired
	private ResponseBuilder builder;
	
	@Autowired
	private TransportService transportService;

	@Autowired
	private DogService dogService;
	
	@Autowired
	private DogRepository dogRepo;
	
	@Autowired
	private PersonRepository personRepo;
	
	public static class TransportData {
		public AddressData address;
		public DogData chooseDog; 
		public LocalDate pickupDate; 
		public PersonData pickupPerson; 
	}
	
	@PostMapping("/submit")
	public @ResponseBody String submit(@RequestBody TransportData data) {
		
		Address address = inflatorService.inflate(data.address); 
		Dog dog = inflatorService.inflate(data.chooseDog);
		Caretaker person = inflatorService.inflatePerson(data.pickupPerson, Caretaker.class);		
		transportService.createTransport(address, dog, person, data.pickupDate);	
		//TODO FIX RETURN OK
		return "OK";
	}
	
	//TODO filter to only active dogs
	//TODO add address autofill < feature >
	@GetMapping("/autofill_dogs")
	public @ResponseBody List<LookupGroup> autofillDog(){
		LookupGroup allDogs = new LookupGroup("All");
		
		for(Dog dog: dogRepo.findAll()) {
			allDogs.addOption(new DogLookupOption(dog));
		}
		
		List<LookupGroup> dogList =  new ArrayList<>();
		dogList.add(allDogs);
		return dogList;
	}
	
	@GetMapping("/autofill_person")
	@Transactional(readOnly=true)
	public @ResponseBody List<LookupGroup> autofillPerson(){
		LookupGroup foster = new LookupGroup("Foster");
		LookupGroup adopter = new LookupGroup("Adopter");
		
		for(Person person: personRepo.findAll() ) {
			if (person.findRole(Foster.class) != null)
					foster.addOption(new PersonLookupOption(person));
			else if (person.findRole(Adopter.class) != null)
					adopter.addOption(new PersonLookupOption(person));
		}
		
		List<LookupGroup> peopleList = new ArrayList<>();
		peopleList.add(adopter);
		peopleList.add(foster);
		
		return peopleList;
	}
	
	public static class ProtoTransportData {
		public Integer dog;
	}
	
	@GetMapping("")
	protected @ResponseBody String loadPage_direct() {
		return builder.buildIndependentPage(loadPage(null));
	}
	

	@PostMapping("")
	public  @ResponseBody PageResponse loadPage(@RequestBody(required = false) ProtoTransportData data) {
		Map<String, Object> vars = new HashMap<>();
		if(data != null) {
			vars.put("dog", data.dog != null ? dogService.findDogByID(data.dog) : null);
		}
		
		return builder.redirect("/register/transport", "forms/transport/transport :: form", vars);
	}
}



