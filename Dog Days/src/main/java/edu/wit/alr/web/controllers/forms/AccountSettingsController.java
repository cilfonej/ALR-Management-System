package edu.wit.alr.web.controllers.forms;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonAlias;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Caretaker;
import edu.wit.alr.database.repository.AccountRepository;
import edu.wit.alr.database.repository.PersonRepository;
import edu.wit.alr.services.PersonService;
import edu.wit.alr.services.inflators.AddressInflator.AddressData;
import edu.wit.alr.services.inflators.InflatorService;
import edu.wit.alr.web.response.PageResponse;
import edu.wit.alr.web.response.Response;
import edu.wit.alr.web.response.ResponseBuilder;
import edu.wit.alr.web.security.UserPrincipal;

@Controller //taha was here
@RequestMapping("/account/settings")
public class AccountSettingsController {
	@Autowired
	private ResponseBuilder builderService;
	
	@Autowired 
	private PersonService personService;
	
	@Autowired //TODO remove
	private PersonRepository personRepo;
	

	@Autowired 
	private AccountRepository accountRepo;
	
	@Autowired
	private InflatorService inflater;
		
	public static class EditData {	
		//public String password;
		
		public String firstName;
		public String lastName;
		public String email;
		public String phone;
		@JsonAlias("home_address")
		public AddressData homeAddress;
		@JsonAlias("mail_address")
		public AddressData mailAddress;
	}
	
	@PostMapping("submit")
	public @ResponseBody Response update(@RequestBody EditData data) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
		Person person = accountRepo.findById((int) principal.getId()).get().getPerson();
		
		Address homeAddress = data.homeAddress == null ? null : inflater.inflate(data.homeAddress);
		Address mailAddress = data.mailAddress == null ? null : inflater.inflate(data.mailAddress);
		
		Person updatePerson = personService.updateUserInfo(person, data.firstName, data.lastName, data.email, data.phone, homeAddress, mailAddress);	
		return null; //TODO show confirm msg (popup maybe - toast - alert )
	}

	@PostMapping("")
	public @ResponseBody PageResponse loadPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
		Person person = accountRepo.findById((int) principal.getId()).get().getPerson();
		
		Map<String, Object> vars = new HashMap<>();
		vars.put("person", person);
		vars.put("isCaretaker", person.findRole(Caretaker.class) != null); //TODO
		
		return builderService.redirect("/account/settings", "forms/account_settings/account_settings :: user_account_all", vars);
	}
	
	@GetMapping("")
	public  @ResponseBody String loadPage_direct() {	
		return builderService.buildIndependentPage(loadPage());
	}
	
}
