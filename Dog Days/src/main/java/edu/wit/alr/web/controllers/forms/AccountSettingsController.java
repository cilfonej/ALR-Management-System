package edu.wit.alr.web.controllers.forms;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.model.Contact.EmailContact;
import edu.wit.alr.database.model.Contact.PhoneContact;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Caretaker;
import edu.wit.alr.database.repository.PersonRepository;
import edu.wit.alr.services.PersonService;
import edu.wit.alr.web.response.PageResponse;
import edu.wit.alr.web.response.Response;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller //taha was here
@RequestMapping("/account/settings")
public class AccountSettingsController {
	@Autowired
	private ResponseBuilder builderService;
	
	@Autowired 
	private PersonService personService;
	
	@Autowired //TODO remove
	private PersonRepository personRepo;
		
	public static class EditData {	
		//public String password;
		public Person person;	//TODO SWITCH TO ACCOUNT LOGGED IN NOT PERSON
		
		public String firstName;
		public String lastName;
		public EmailContact email;
		public PhoneContact phone;
		public Address homeAddress;
		public Address mailAddress;
	}
	
	@PostMapping() //TODO NEEDS POST MAPPING TO ACTUALLY SAVE AND WORK
	public @ResponseBody Response update(@RequestBody EditData data) {
		Person updatePerson = personService.updateUserInfo(data.person, data.firstName, data.lastName, data.email, data.phone, data.homeAddress, data.mailAddress);	
		return null; //TODO show confirm msg (popup maybe - toast - alert )
	}
	
	public @ResponseBody PageResponse loadPage() {
		
		Map<String, Object> vars = new HashMap<>();
		vars.put("person", personRepo.findAll().iterator().next());   //TODO get currently logged in person for value + remove above todo
		vars.put("isCaretaker", personRepo.findAll().iterator().next().findRole(Caretaker.class) != null); //TODO
		
		return builderService.redirect("/account/settings", "forms/account_settings/account_settings :: user_account_all", vars);
	}
	
	@GetMapping("")
	public  @ResponseBody String loadPage_direct() {	
		return builderService.buildIndependentPage(loadPage());
	}
	
}
