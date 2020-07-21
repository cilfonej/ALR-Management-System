package edu.wit.alr.web.controllers.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.model.Contact.EmailContact;
import edu.wit.alr.database.model.Contact.PhoneContact;
import edu.wit.alr.database.model.Dog;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Adopter;
import edu.wit.alr.database.model.roles.ApplicationCoordinator;
import edu.wit.alr.database.model.roles.Foster;
import edu.wit.alr.database.repository.DogRepository;
import edu.wit.alr.services.PersonService;
import edu.wit.alr.services.inflators.AddressInflator.AddressData;
import edu.wit.alr.services.inflators.DogInflator.DogData;
import edu.wit.alr.services.inflators.InflatorService;
import edu.wit.alr.web.lookups.DogLookupOption;
import edu.wit.alr.web.lookups.LookupOption.LookupGroup;
import edu.wit.alr.web.response.Response;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller
@RequestMapping("/edit/person")
public class EditPersonController {

	@Autowired
	private InflatorService inflator;
	@Autowired
	private PersonService people;
	@Autowired
	private DogRepository dogRepo;
	@Autowired
	private ViewPersonController viewController;
	@Autowired
	private ResponseBuilder builder;

	public static class EditPersonData {
		public String firstname;
		public String lastname;
		public String email;
		public String phone;

		public AddressData home_address;
		public AddressData mail_address;
	}

	@PostMapping("/{id}")
	public @ResponseBody Response update(@PathVariable("id") Integer id, @RequestBody EditPersonData data) {
		Address home_address = data.home_address == null ? null : inflator.inflate(data.home_address);
		Address mail_address = data.mail_address == null ? null : inflator.inflate(data.mail_address);
		Person person = people.updatePersonDetails(people.findPersonByID(id), data.firstname, data.lastname, new EmailContact(data.email),
				new PhoneContact(data.phone), home_address, mail_address);
		return viewController.updatePersonHeader(person);
	}   

	@GetMapping("/autofill_dogs")
	public @ResponseBody List<LookupGroup> autofillDog() {
		LookupGroup allDogs = new LookupGroup("All");

		for (Dog dog : dogRepo.findAll()) {
			allDogs.addOption(new DogLookupOption(dog));
		}

		List<LookupGroup> dogList = new ArrayList<>();
		dogList.add(allDogs);
		return dogList;
	}

	public static class UpdateDogData {
		public DogData dog;
	}

	@PostMapping("/{id}/dog/coordinator")
	public @ResponseBody Response updateDogCoordinator(@PathVariable("id") Integer id,
			@RequestBody UpdateDogData data) {
		Dog dog = inflator.inflate(data.dog);
		Person person = people.findPersonByID(id);
		ApplicationCoordinator coordinator = person.findRole(ApplicationCoordinator.class);
		dog.setAddoptionCoordinator(coordinator);
		dogRepo.save(dog);

		Map<String, Object> vars = new HashMap<>();
		vars.put("coordinatorList", List.of(dog));

		return builder.replacement("#coordinatorCard",
				"pages/people/view/view_dog_list/view_coordinator_dog :: card_content", vars);
	}

	@PostMapping("/{id}/dog/adopter")
	public @ResponseBody Response updateDogAdopter(@PathVariable("id") Integer id, @RequestBody UpdateDogData data) {
		Dog dog = inflator.inflate(data.dog);
		Person person = people.findPersonByID(id);
		Adopter adopter = person.findRole(Adopter.class);
		dog.setCaretaker(adopter);
		dogRepo.save(dog);

		Map<String, Object> vars = new HashMap<>();
		vars.put("adoptList", List.of(dog));

		return builder.replacement("#adoptCard", "pages/people/view/view_dog_list/view_adopt_dog :: card_content",
				vars);
	}

	@PostMapping("/{id}/dog/foster")
	public @ResponseBody Response updateDogFoster(@PathVariable("id") Integer id, @RequestBody UpdateDogData data) {
		Dog dog = inflator.inflate(data.dog);
		Person person = people.findPersonByID(id);
		Foster foster = person.findRole(Foster.class);
		dog.setCaretaker(foster);
		dogRepo.save(dog);

		Map<String, Object> vars = new HashMap<>();
		vars.put("fosterList", List.of(dog));

		return builder.replacement("#fosterCard", "pages/people/view/view_dog_list/view_foster_dog :: card_content",
				vars);
	}
}
