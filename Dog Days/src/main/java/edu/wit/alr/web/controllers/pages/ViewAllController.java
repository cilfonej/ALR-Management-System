package edu.wit.alr.web.controllers.pages;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.database.repository.DogRepository;
import edu.wit.alr.database.repository.PersonRepository;
import edu.wit.alr.web.response.ReplaceResponse;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller
@RequestMapping("/view/{type}")
public class ViewAllController {
	
	@Autowired private DogRepository dogRepository;
	@Autowired private PersonRepository personRepository;
	
	@Autowired
	private ResponseBuilder builder;
	
	public static class SearchParams {
		public String nameOrId;
	}
	
	@PostMapping("/search")
	public @ResponseBody ReplaceResponse search(@PathVariable("type") String type, @RequestBody SearchParams param) {
		String search = param.nameOrId;
		if(search == null) search = "";	// ensure there is some value
		
		if(search.startsWith("#")) 			// if they started with #,
			search = search.substring(1);	// strip it
		search = search.trim() + "%";	// trim whitespace and append wild-card
		
		Map<String, Object> vars = Map.of("results", 
											type.equals("dogs") ? dogRepository.findAllByFuzzyNameOrId(search) : 
											type.equals("people") ? personRepository.findAllByFuzzyNameOrId(search) : 
												Collections.emptyList());
		return builder.replacement(".view-all_results", 
				type.equals("dogs") ? "/pages/dog/list/list_dogs :: searchResults" : 
				type.equals("people") ? "/pages/people/list/list_people :: searchResults" : null, vars);
	}
	
	@PostMapping("/render/{id}")
	public @ResponseBody ReplaceResponse render(@PathVariable("type") String type, @PathVariable("id") int id) {
		return builder.replacement(
				".view-all_display_wrapper", 
				
				type.equals("dogs") ? "/pages/dog/list/list_dogs :: viewResult" : // TODO: validate Dog
				type.equals("people") ? "/pages/people/list/list_people :: viewResult" : 
					null,	
						
				Map.of("obj", 
						type.equals("dogs") ? dogRepository.findById(id).orElseThrow() : 
						type.equals("people") ? personRepository.findById(id).orElseThrow() : null)
			);
	}
}
