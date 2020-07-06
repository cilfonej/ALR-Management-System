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
import edu.wit.alr.services.PersonService;
import edu.wit.alr.services.inflators.InflatorService;
import edu.wit.alr.services.inflators.PersonInflator.PersonData;
import edu.wit.alr.web.lookups.LookupOption.LookupGroup;
import edu.wit.alr.web.lookups.PersonLookupOption.AdopterLookupOption;
import edu.wit.alr.web.lookups.PersonLookupOption.FostertLookupOption;
import edu.wit.alr.web.response.Response;

@Controller
@RequestMapping("/edit/person")
public class EditPersonController {
	
	@Autowired private InflatorService inflator;
	@Autowired private PersonService people;
	
	@Autowired private ViewPersonController viewController;

	public static class EditPersonData {
		public String firstname;
		public String lastname;
	}
	
	@PostMapping("/{id}")
	public @ResponseBody Response update(@PathVariable("id") Integer id, @RequestBody EditPersonData data) {
		Person person = people.updatePersonDetails(people.findPersonByID(id), data.firstname, data.lastname);
		return viewController.updatePersonHeader(person);
	}

	public static class EditRoleData {
	}
	
	@PostMapping("/{id}/role")
	public @ResponseBody Response updateCaretaker(@PathVariable("id") Integer id, @RequestBody EditRoleData data) {
		
		return null;
	}
}
