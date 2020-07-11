package edu.wit.alr.web.controllers.forms;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.database.model.Dog;
import edu.wit.alr.database.model.DogReturn;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Adopter;
import edu.wit.alr.database.model.roles.Caretaker;
import edu.wit.alr.database.model.roles.Foster;
import edu.wit.alr.database.repository.DogRepository;
import edu.wit.alr.database.repository.PersonRepository;
import edu.wit.alr.services.ReturnDogService;
import edu.wit.alr.services.inflators.AddressInflator.AddressData;
import edu.wit.alr.services.inflators.DogInflator.DogData;
import edu.wit.alr.services.inflators.InflatorService;
import edu.wit.alr.services.inflators.PersonInflator.PersonData;
import edu.wit.alr.web.controllers.pages.ReturnDogViewController;
import edu.wit.alr.web.lookups.DogLookupOption;
import edu.wit.alr.web.lookups.LookupOption.LookupGroup;
import edu.wit.alr.web.lookups.PersonLookupOption;
import edu.wit.alr.web.response.PageResponse;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller
@RequestMapping("/register/return")
public class DogReturnController {
	
	@Autowired
	private InflatorService inflatorService;
	
	@Autowired
	private DogRepository dogRepo;
	
	@Autowired
	private PersonRepository personRepo;
	
	@Autowired
	private ResponseBuilder builderService;
	
	@Autowired
	private ReturnDogService returnDogService;
	
	@Autowired
	private ReturnDogViewController viewController;
	
	public static class ReturnData {
		public AddressData address;
		public DogData returnDog; 
		public PersonData returnPerson; 
		public PersonData newPerson; 
		public LocalDate returnDate; 
		public String reason;
	}

	@PostMapping("submit")
	public @ResponseBody PageResponse returnSubmit(@RequestBody ReturnData data) {
		Dog dog = inflatorService.inflate(data.returnDog);
		
		Adopter returnPerson = inflatorService.inflatePerson(data.returnPerson, Adopter.class);
		Caretaker newPerson = inflatorService.inflatePerson(data.newPerson, Caretaker.class);	
		
		String reason = data.reason;
		LocalDate returnDate = LocalDate.now();
		
		DogReturn dogReturn = returnDogService.createReturnDog(dog, returnPerson, newPerson, reason, returnDate);	
		return viewController.loadPage(dogReturn);
	}
	
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
	
	@GetMapping("/autofill_adopter")
	@Transactional(readOnly=true)
	public @ResponseBody List<LookupGroup> autofillAdopter(){
		LookupGroup adopter = new LookupGroup("Adopter");
		
		for(Person person: personRepo.findAll() ) {
			if (person.findRole(Adopter.class) != null)
				adopter.addOption(new PersonLookupOption(person));
		}
		
		List<LookupGroup> peopleList = new ArrayList<>();
		peopleList.add(adopter);
		
		
		return peopleList;
	}
	
	@GetMapping("/autofill_foster")
	@Transactional(readOnly=true)
	public @ResponseBody List<LookupGroup> autofillFoster(){
		LookupGroup foster = new LookupGroup("Foster");
		
		for(Person person: personRepo.findAll() ) {
			if (person.findRole(Foster.class) != null)
				foster.addOption(new PersonLookupOption(person));
		}
		
		List<LookupGroup> peopleList = new ArrayList<>();
		peopleList.add(foster);
	
		return peopleList;
	}
	
	@GetMapping("")
	public @ResponseBody String returnUrl() {
		return builderService.buildIndependentPage(returnRedirect());
	}
	
	@PostMapping("")
	public @ResponseBody PageResponse returnRedirect() {
		return builderService.redirect("/forms/return_dog", "forms/return_dog/return_dog::form", null);
	}
}