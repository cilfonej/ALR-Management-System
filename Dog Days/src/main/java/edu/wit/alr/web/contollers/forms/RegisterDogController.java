package edu.wit.alr.web.contollers.forms;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonAlias;

import edu.wit.alr.database.model.Dog.Gender;
import edu.wit.alr.database.model.roles.ApplicationCoordinator;
import edu.wit.alr.database.repository.PersonRepository;
import edu.wit.alr.services.DogService;
import edu.wit.alr.services.inflators.InflatorService;
import edu.wit.alr.services.inflators.PersonInflator.PersonData;
import edu.wit.alr.web.lookups.LookupOption.LookupGroup;
import edu.wit.alr.web.lookups.PersonLookupOption.AdminLookupOption;

@Controller
@RequestMapping("/forms/redgister/dog")
public class RegisterDogController {

	@Autowired private InflatorService inflator;
	@Autowired private DogService dogs;
	
	@Autowired
	// TODO: remove / relocate
	private PersonRepository personRepository;
	
	public static class RegisterData {
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
	
	@PostMapping("")
	public @ResponseBody String register(@RequestBody RegisterData data) {
		ApplicationCoordinator coordinator = inflator.inflatePerson(data.coordinator, ApplicationCoordinator.class);
		
		dogs.createDog(data.name, Gender.parse(data.gender), data.weight, 
				data.bday_year, data.bday_month, data.bday_day, data.heart_date, data.ft_date, 
				coordinator, data.recruiter, data.description);
		
		System.out.println(coordinator.getName());
		return "OK";
	}
	
	@GetMapping("/list/people")
	@Transactional(readOnly = true)
	public @ResponseBody List<LookupGroup> listPeople() {
//		ArrayList<Person> people = new ArrayList<>();
//		personRepository.findAll().forEach(people::add);
//		people.sort((a, b) -> a.getName().compareTo(b.getName()));
//
//		List<LookupGroup> groups = new ArrayList<>();
//		LookupGroup adminsGroup = new LookupGroup("Admins");
//		people.stream()
//			.map(person -> person.findRole(Admin.class)).filter(a -> a != null)
//			.map(AdminLookupOption::new).forEach(adminsGroup::addOption);
//		groups.add(adminsGroup);
//		
//		LookupGroup fosterGroup = new LookupGroup("Foster");
//		people.stream()
//			.map(person -> person.findRole(Foster.class)).filter(a -> a != null)
//			.map(FostertLookupOption::new).forEach(fosterGroup::addOption);
//		groups.add(fosterGroup);
//		
//		LookupGroup adopterGroup = new LookupGroup("Adopter");
//		people.stream()
//			.map(person -> person.findRole(Adopter.class)).filter(a -> a != null)
//			.map(AdopterLookupOption::new).forEach(adopterGroup::addOption);
//		groups.add(adopterGroup);
//		
//		LookupGroup allGroup = new LookupGroup("All");
//		people.stream()
//			.map(PersonLookupOption::new).forEach(allGroup::addOption);
//		groups.add(allGroup);
		
		List<LookupGroup> groups = new ArrayList<>();
		LookupGroup adminsGroup = new LookupGroup("Application Coordinators");
		StreamSupport.stream(personRepository.findAll().spliterator(), false)
			.map(person -> person.findRole(ApplicationCoordinator.class)).filter(a -> a != null)
			.map(AdminLookupOption::new).forEach(adminsGroup::addOption);
		groups.add(adminsGroup);
		
		return groups;
	}
}
