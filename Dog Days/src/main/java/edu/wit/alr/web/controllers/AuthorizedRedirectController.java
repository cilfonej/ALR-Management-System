package edu.wit.alr.web.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import edu.wit.alr.services.AuthRedirectService;

@Controller
public class AuthorizedRedirectController {
	
	@Autowired
	private AuthRedirectService service;
	
	@GetMapping("/r/{key}")
	public String redirect(@PathVariable("key") String key, HttpServletRequest request) {
		return "forward:/" + service.setupForward(key, request);
	}
}
