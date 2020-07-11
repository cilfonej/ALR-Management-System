package edu.wit.alr.web.controllers.pages;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.database.model.DogReturn;
import edu.wit.alr.services.ReturnDogService;
import edu.wit.alr.web.response.PageResponse;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller
@RequestMapping("/view/returns")
public class ReturnDogViewController {
	
	@Autowired
	private ResponseBuilder builderService;
	
	@Autowired
	private ReturnDogService returnDogService;
	
	@GetMapping("/{id}")
	protected @ResponseBody String loadPage_direct(@PathVariable("id") Integer id) {
		return builderService.buildIndependentPage(loadPage(id));
	}
	
	public PageResponse loadPage(int return_id) {
		return loadPage(returnDogService.findReturnByID(return_id));
	}
	
	public PageResponse loadPage(DogReturn dogReturn) {
		Map<String, Object> vars = new HashMap<>();
		vars.put("dog", dogReturn.getDog());
		vars.put("returner", dogReturn.getAddopter());
		vars.put("reason", dogReturn.getReason());
		vars.put("returnDate", dogReturn.getReturnDate());
		vars.put("returnID", dogReturn.getId());
		vars.put("dogReturn", dogReturn);
		
		return builderService.redirect("/view/returns/" + dogReturn.getId(), "pages/return_dog/view/return_dog_view :: page", vars);
	}
		
}