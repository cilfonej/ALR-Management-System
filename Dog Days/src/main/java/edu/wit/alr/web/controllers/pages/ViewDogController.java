package edu.wit.alr.web.controllers.pages;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.database.model.Dog;
import edu.wit.alr.services.DogService;
import edu.wit.alr.web.response.PageResponse;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller
@RequestMapping("/view/dogs")
public class ViewDogController {

	@Autowired private ResponseBuilder builder;
	
	@Autowired private DogService dogService;
	
	@GetMapping("")
	protected @ResponseBody String loadPage_direct(@RequestParam("id") int id) {
		return builder.buildIndependentPage(loadPage(id));
	}
	
	public @ResponseBody PageResponse loadPage(int dog_id) {
		return loadPage(dogService.findDogByID(dog_id));
	}
	
	public @ResponseBody PageResponse loadPage(Dog dog) {
		if(dog == null) ; // TODO: build error page
		return builder.redirect("/view/dogs?id=" + dog.getID(), "pages/dog/view/view_dog :: page", Map.of("dog", dog));
	}
}
