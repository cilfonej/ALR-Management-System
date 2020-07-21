package edu.wit.alr.web.controllers.pages;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.database.model.Dog;
import edu.wit.alr.services.DogService;
import edu.wit.alr.services.ReturnDogService;
import edu.wit.alr.services.TransportService;
import edu.wit.alr.web.response.PageResponse;
import edu.wit.alr.web.response.ReplaceResponse;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller
@RequestMapping("/view/dogs")
public class ViewDogController {

	@Autowired private ResponseBuilder builder;
	
	@Autowired private DogService dogService;
	@Autowired private TransportService transportService;
	@Autowired private ReturnDogService returnService;
	
	@GetMapping("")
	protected @ResponseBody String list_direct() {
		return builder.buildIndependentPage(listPage());
	}
	
	@GetMapping("/{id}")
	public @ResponseBody String loadPage_direct(@PathVariable(value="id", required=false) Integer id) {
		if(id == null) return list_direct();
		return builder.buildIndependentPage(loadPage(id));
	}

	@PostMapping("/{id}")
	public @ResponseBody PageResponse loadPage(@PathVariable("id") int dog_id) {
		return loadPage(dogService.findDogByID(dog_id));
	}
	
	public PageResponse loadPage(Dog dog) {
		if(dog == null) ; // TODO: build error page
		
		Map<String, Object> vars = new HashMap<>();
		vars.put("reservation", transportService.findReservationForDog(dog));
		vars.put("dog", dog);
		vars.put("returnDog", returnService.findReturnByDog(dog));
		
		return builder.redirect("/view/dogs/" + dog.getId(), "pages/dog/view/view_dog :: page", vars);
	}

	@PostMapping("")
	public @ResponseBody PageResponse listPage() {
		return builder.redirect("/view/dogs", "pages/dog/list/list_dogs :: page", null);
	}
	
	public ReplaceResponse updateDogInfo(Dog dog) {
		return builder.replacement(
				".dog-card", 
				"pages/dog/view/dog_info_card :: card", 
				Map.of("dog", dog));
	}
	
	public ReplaceResponse updateCaretakerCard(Dog dog) {
		return builder.replacement(
				".caretaker-card", 
				"pages/dog/view/caretaker_assignment_card :: card", 
				Map.of("dog", dog));
	}
}
