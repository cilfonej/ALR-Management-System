package edu.wit.alr.web.controllers.forms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.services.inflators.InflatorService;
import edu.wit.alr.web.response.PageResponse;
import edu.wit.alr.web.response.Response;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller
@RequestMapping("/register/person")
public class CreatePersonController {

	@Autowired private InflatorService inflator;
	
	@Autowired private ResponseBuilder builder;
	
	
	public static class RegisterData {
	}
	
	@PostMapping("submit")
	public @ResponseBody Response register(@RequestBody RegisterData data) {
		return null;
	}
	
	@GetMapping("")
	protected @ResponseBody String loadPage_direct() {
		return builder.buildIndependentPage(loadPage());
	}
	
	public @ResponseBody PageResponse loadPage() {
		return builder.redirect("/register/person", "forms/create_person/create_person :: form", null);
	}
}