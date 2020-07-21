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
import edu.wit.alr.database.repository.DogRepository;
import edu.wit.alr.services.PersonService;
import edu.wit.alr.services.inflators.AddressInflator;
import edu.wit.alr.services.inflators.PersonInflator;
import edu.wit.alr.web.response.PageResponse;
import edu.wit.alr.web.response.ReplaceResponse;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller
@RequestMapping("/view/people")
public class ViewPersonController {

	@Autowired private ResponseBuilder builder;
	@Autowired private PersonService personService;
	@Autowired private DogRepository dogRepo;
	@Autowired private AddressInflator addressInflator;
	@Autowired private PersonInflator personInflator;
	
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
	
	public PageResponse loadPage(int person_id) {
		return loadPage(personService.findPersonByID(person_id));
	}
	
	public PageResponse loadPage(Person person) {
		if(person == null) ; // TODO: build error page
		
		
		Map<String, Object> vars = new HashMap<>();
		vars.put("person", person);
		vars.put("adoptList", dogRepo.findAllByAdopter(person));
		vars.put("fosterList", dogRepo.findAllByFoster(person));
		vars.put("roleAdopter", person.findRole(Adopter.class) != null);
		vars.put("roleFoster", person.findRole(Foster.class) != null);
		vars.put("roleCoordinator", person.findRole(ApplicationCoordinator.class) != null);
		
		return builder.redirect("/view/people/" + person.getId(), "pages/people/view/view_person :: page", vars);
	}
	
	public ReplaceResponse updatePersonHeader(Person person) {
		return builder.replacement(
				".person-header", 
				"pages/people/view/person_header_card :: card", 
				Map.of("person", person));
		//TODO updapte person-info_all card at same time ^  (basic info card)
	}
}
