package edu.wit.alr.web.controllers.forms;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Role;
import edu.wit.alr.services.PersonService;
import edu.wit.alr.services.inflators.AddressInflator.AddressData;
import edu.wit.alr.services.inflators.InflatorService;
import edu.wit.alr.web.response.PageResponse;
import edu.wit.alr.web.response.Response;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller
@RequestMapping("/register/person")
public class CreatePersonController {

	@Autowired private PersonService personService;
	@Autowired private InflatorService inflator;
	
	@Autowired private ResponseBuilder builder;
	
	public static class RegisterData {
		public String firstname;
		public String lastname;
		
		public String[] roles;
		
		public String email;
		public String phone;
		
		public AddressData home_address;
		public AddressData mail_address;
	}
	
	@PostMapping("submit")
	public @ResponseBody Response register(@RequestBody RegisterData data) {
		Address home_address = data.home_address == null ? null : inflator.inflate(data.home_address);
		Address mail_address = data.mail_address == null ? null : inflator.inflate(data.mail_address);
		
		List<Class<? extends Role>> roles = personService.toRoles(data.roles);
		
		Person person = personService.createPerson(data.firstname, data.lastname, 
				data.email, data.phone, home_address, mail_address, roles);
		
		return null;
	}
	
	@GetMapping("")
	protected @ResponseBody String loadPage_direct() {
		return builder.buildIndependentPage(loadPage());
	}
	
	@PostMapping("")
	public @ResponseBody PageResponse loadPage() {
		return builder.redirect("/register/person", "forms/create_person/create_person :: form", null);
	}
}