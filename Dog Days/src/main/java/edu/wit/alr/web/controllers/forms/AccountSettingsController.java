package edu.wit.alr.web.controllers.forms;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.database.model.Person;
import edu.wit.alr.services.PersonService;
import edu.wit.alr.services.inflators.AddressInflator.AddressData;
import edu.wit.alr.web.controllers.forms.TransportController.ProtoTransportData;
import edu.wit.alr.web.response.PageResponse;
import edu.wit.alr.web.response.Response;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller //taha was here
@RequestMapping("/account_settings")
public class AccountSettingsController {
	@Autowired
	private ResponseBuilder builderService;
	
	@Autowired 
	private PersonService personService;
		
	public static class EditData {
		public String email; //TODO fix type if type gets changed from string
		public String password;
		
		public String firstName;
		public String lastName;
		public String phone;
		public AddressData address;
		
		public Person person; //TODO REMOVE
	}
	
	@PostMapping("/{id}")
	public @ResponseBody Response update(@PathVariable("id") Integer id, @RequestBody EditData data) {
		Person updatePerson = personService.updateUser(data.person, data.email, data.password, data.firstName, data.lastName, data.phone, data.address);
		//TODO fix data.person to data by id
		
		Map<String, Object> vars = new HashMap<>();
		//TODO GET EMAIL vars.put("email", updatePerson.get)
		//TODO GET PASSWORD vars.put("password", updatePerson.get)
		vars.put("firstName", updatePerson.getFirstname());
		vars.put("lastName", updatePerson.getLastname());
		//TODO GET PHONE vars.put("phone", updatePerson.get);
		//TODO GET ADDRESS vars.put("address", updatePerson.get);
		
		return null;
	}
	
	@GetMapping("")
	public  @ResponseBody String loadPage() {	
		return builderService.buildIndependentPage(builderService.redirect("/account_settings", "forms/account_settings/account_settings :: user_account_all", null));
	}
	
}
