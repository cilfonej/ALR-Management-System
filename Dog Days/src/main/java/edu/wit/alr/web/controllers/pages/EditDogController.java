package edu.wit.alr.web.controllers.pages;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonAlias;

import edu.wit.alr.database.model.Dog;
import edu.wit.alr.database.model.Dog.Gender;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Adopter;
import edu.wit.alr.database.model.roles.ApplicationCoordinator;
import edu.wit.alr.database.model.roles.Caretaker;
import edu.wit.alr.database.model.roles.Foster;
import edu.wit.alr.database.repository.PersonRepository;
import edu.wit.alr.services.DogService;
import edu.wit.alr.services.inflators.InflatorService;
import edu.wit.alr.services.inflators.PersonInflator.PersonData;
import edu.wit.alr.web.lookups.LookupOption.LookupGroup;
import edu.wit.alr.web.lookups.PersonLookupOption.AdopterLookupOption;
import edu.wit.alr.web.lookups.PersonLookupOption.FostertLookupOption;
import edu.wit.alr.web.response.Response;

@Controller
@RequestMapping("/edit/dog")
public class EditDogController {
	
	@Autowired private InflatorService inflator;
	@Autowired private DogService dogs;
	
	@Autowired private ViewDogController viewController;

	@Autowired
	// TODO: remove / relocate
	private PersonRepository personRepository;
	
	public static class EditData {
		@JsonAlias("dog-name")
		public String name;

		@JsonAlias("bday-year") public int bday_year;
		@JsonAlias("bday-month") public Integer bday_month;
		@JsonAlias("bday-day") public Integer bday_day;

		@JsonAlias("dog-gender")
		public String gender;
		public Double weight;
		
		public PersonData coordinator;
		public String recruiter;
		
		public String description;
		
		@JsonAlias("flee_and_tic-date")
		public LocalDate ft_date;
	
		@JsonAlias("heartworm-date")
		public LocalDate heart_date;
	}
	
	@PostMapping("/{id}")
	public @ResponseBody Response update(@PathVariable("id") Integer id, @RequestBody EditData data) {
		ApplicationCoordinator coordinator = inflator.inflatePerson(data.coordinator, ApplicationCoordinator.class);
		
		Dog dog = dogs.updateDog(dogs.findDogByID(id), 
				data.name, Gender.parse(data.gender), data.weight, 
				data.bday_year, data.bday_month, data.bday_day, data.heart_date, data.ft_date, 
				coordinator, data.recruiter, data.description);
		
		return viewController.updateDogInfo(dog);
	}

	public static class CaretakerData {
		public PersonData caretaker;
	}
	
	@PostMapping("/{id}/caretaker")
	public @ResponseBody Response updateCaretaker(@PathVariable("id") Integer id, @RequestBody(required=false) CaretakerData data) {
		Caretaker caretaker = data == null || data.caretaker == null ? null : inflator.inflatePerson(data.caretaker, Caretaker.class);
		Dog dog = dogs.findDogByID(id);
		
		dogs.updateCaretaker(dog, caretaker);
		
		return viewController.updateCaretakerCard(dog);
	}
	
	@GetMapping("/list/caretakers")
	@Transactional(readOnly = true)
	public @ResponseBody List<LookupGroup> listPeople() {
		ArrayList<Person> people = new ArrayList<>();
		personRepository.findAll().forEach(people::add);
		people.sort((a, b) -> a.getName().compareTo(b.getName()));

		List<LookupGroup> groups = new ArrayList<>();
		LookupGroup fosterGroup = new LookupGroup("Foster");
		people.stream()
			.map(person -> person.findRole(Foster.class)).filter(a -> a != null)
			.map(FostertLookupOption::new).forEach(fosterGroup::addOption);
		groups.add(fosterGroup);
		
		LookupGroup adopterGroup = new LookupGroup("Adopter");
		people.stream()
			.map(person -> person.findRole(Adopter.class)).filter(a -> a != null)
			.map(AdopterLookupOption::new).forEach(adopterGroup::addOption);
		groups.add(adopterGroup);
		
		return groups;
	}
}
