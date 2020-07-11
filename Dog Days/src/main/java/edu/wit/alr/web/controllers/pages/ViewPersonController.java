package edu.wit.alr.web.controllers.pages;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.model.roles.Adopter;
import edu.wit.alr.database.model.roles.ApplicationCoordinator;
import edu.wit.alr.database.model.roles.Foster;
import edu.wit.alr.database.model.roles.Role;
import edu.wit.alr.services.PersonService;
import edu.wit.alr.web.response.PageResponse;
import edu.wit.alr.web.response.ReplaceResponse;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller
@RequestMapping("/view/people")
public class ViewPersonController {

	@Autowired private ResponseBuilder builder;
	@Autowired private PersonService personService;
	
	@GetMapping("")
	protected @ResponseBody String list_direct() {
//		return builder.buildIndependentPage(listPage());
		return null;
	}
	
	@GetMapping("/{id}")
	protected @ResponseBody String loadPage_direct(@PathVariable("id") Integer id) {
		if(id == null) return list_direct();
		return builder.buildIndependentPage(loadPage(id));
	}
	
	public PageResponse loadPage(int dog_id) {
		return loadPage(personService.findPersonByID(dog_id));
	}
	
	public PageResponse loadPage(Person person) {
		if(person == null) ; // TODO: build error page
		
		Map<String, Object> vars = new HashMap<>();
		vars.put("person", person);
		
		return builder.redirect("/view/people/" + person.getId(), "pages/people/view/view_person :: page", vars);
	}
	
	public ReplaceResponse updatePersonHeader(Person person) {
		return builder.replacement(
				".person-header", 
				"pages/people/view/person_header_card :: card", 
				Map.of("person", person));
	}
	
	public ReplaceResponse updateRoleCard(Role role) {
		String clazz = "$.role-card_";
		
		if(role instanceof Adopter) clazz += "adopter";
		else if(role instanceof Foster) clazz += "foster";
		else if(role instanceof ApplicationCoordinator) clazz += "coordinator";
		
		return builder.replacement(
				clazz + " $+.view-person_roles", 
				"pages/people/view/role_card :: card", 
				Map.of("role", role, "person", role.getPerson()));
	}
}
