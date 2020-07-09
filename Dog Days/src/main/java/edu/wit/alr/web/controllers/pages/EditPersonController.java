package edu.wit.alr.web.controllers.pages;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonAlias;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Role;
import edu.wit.alr.services.PersonService;
import edu.wit.alr.services.inflators.AddressInflator.AddressData;
import edu.wit.alr.services.inflators.InflatorService;
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
		@JsonAlias("roles")
		public String role;
		
		public String email;
		public String phone;
		
		public AddressData home_address;
		public AddressData mail_address;
	}

	@PostMapping("/{id}/role")
	public @ResponseBody Response updateRole(@PathVariable("id") Integer id, @RequestBody EditRoleData data) {
		Address home_address = data.home_address == null ? null : inflator.inflate(data.home_address);
		Address mail_address = data.mail_address == null ? null : inflator.inflate(data.mail_address);
		
		List<Class<? extends Role>> roles = people.toRoles(data.role);
		if(roles.size() < 1) throw new IllegalArgumentException("No such Role: " + data.role);
		
		Role role = people.constructRole(people.findPersonByID(id), roles.get(0), data.email, data.phone, home_address, mail_address);
		return viewController.updateRoleCard(role);
	}
}
