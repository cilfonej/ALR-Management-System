package edu.wit.alr.web.controllers.forms;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonAlias;

import edu.wit.alr.database.model.DogReturn;
import edu.wit.alr.services.ReturnDogService;
import edu.wit.alr.web.response.Response;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller
@RequestMapping("/edit/return")
public class ReturnDogViewEditController {
	
	@Autowired
	private ReturnDogService returnDogService;
	
	@Autowired
	private ResponseBuilder builderService;

	public static class EditData {
		@JsonAlias("returnDate")
		public LocalDate date;
		
		@JsonAlias("reason")
		public String reason;
	}
	
	@PostMapping("/{id}")
	public @ResponseBody Response update(@PathVariable("id") Integer id, @RequestBody EditData data) {	
		DogReturn updateReturn = returnDogService.updateReturn(returnDogService.findReturnByID(id), data.date, data.reason);
		
		Map<String, Object> vars = new HashMap<>();
		vars.put("returnDate", updateReturn.getReturnDate());
		vars.put("reason",updateReturn.getReason());
		vars.put("dogReturn", updateReturn);
		
		return builderService.replacement(".returnCard", "forms/return_dog/return_dog_view::pageReturnCard", vars);
	}
}