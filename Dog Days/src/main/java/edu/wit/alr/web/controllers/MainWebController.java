package edu.wit.alr.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import edu.wit.alr.config.Demo;

@Controller
public class MainWebController {
	@Autowired
	private Demo demo;
	
	@GetMapping("/gen_data")
	public @ResponseBody String gen_data(@RequestParam(name="seed", defaultValue="0") int seed) {
		demo.generateTestData(seed);
		return "ok";
	}
	
	@GetMapping("/")
	public ModelAndView index() {
		ModelAndView view = new ModelAndView("index");
		return view;
	}
}
