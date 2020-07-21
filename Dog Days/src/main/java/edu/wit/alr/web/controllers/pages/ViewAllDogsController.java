package edu.wit.alr.web.controllers.pages;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.database.repository.DogRepository;
import edu.wit.alr.web.response.ReplaceResponse;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller
@RequestMapping("/view/{type}")
public class ViewAllDogsController {
	
	@Autowired
	private DogRepository repository;
	
	@Autowired
	private ResponseBuilder builder;
	
	public static class SearchParams {
		public String nameOrId;
	}
	
	@PostMapping("/search")
	public @ResponseBody ReplaceResponse search(@RequestBody SearchParams param) {
		String search = param.nameOrId;
		if(search == null) search = "";	// ensure there is some value
		
		if(search.startsWith("#")) 			// if they started with #,
			search = search.substring(1);	// strip it
		search = search.trim() + "%";	// trim whitespace and append wild-card
		
		Map<String, Object> vars = Map.of("dogs", repository.findAllByFuzzyNameOrId(search));
		return builder.replacement(".view-all_results", "/pages/dog/list/list_dogs :: searchResults", vars);
	}
	
	@PostMapping("/render/{id}")
	public @ResponseBody ReplaceResponse render(@PathVariable("id") int id) {
		return builder.replacement(
				".view-all_display_wrapper", 
				"/pages/dog/list/list_dogs :: viewResult", // TODO: validate Dog
				Map.of("dog", repository.findById(id).orElseThrow())
			);
	}
}
