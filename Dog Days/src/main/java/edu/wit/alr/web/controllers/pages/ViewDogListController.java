package edu.wit.alr.web.controllers.pages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.wit.alr.database.repository.DogRepository;

@Controller
@RequestMapping("")
public class ViewDogListController {
	
	@Autowired
	private DogRepository dogRepo;
	
	//TODO get list functions for adopted / coordinator / foster / return dogs
}
