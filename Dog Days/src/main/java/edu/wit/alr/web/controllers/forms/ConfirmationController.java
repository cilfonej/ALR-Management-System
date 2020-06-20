package edu.wit.alr.web.controllers.forms;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.database.repository.DogRepository;
import edu.wit.alr.services.DogService;

@Controller
@RequestMapping("/forms")
public class ConfirmationController {
	
	@Autowired
	private DogService confirmationService;
	
	//TODO remove when change dog vvv is done
	@Autowired
	private DogRepository dogRepo;
	
	public static class ConfirmationData {
		public double weightInfo;
		public LocalDate medDate;
		public Integer birthMonth;
		public Integer birthDay;
		public Integer birthYear;
	}
	
	@PostMapping("/confirmation")
	public @ResponseBody String confirmationSubmit(@RequestBody ConfirmationData data) {
		double weight = data.weightInfo;
		LocalDate medDate = data.medDate;
		Integer birthMonth = data.birthMonth;
		Integer birthDay = data.birthDay;
		Integer birthYear = data.birthYear;
		//TODO change dog vvv
		confirmationService.updateInfo(dogRepo.findAll().iterator().next(), weight, medDate, birthMonth, birthDay, birthYear);
		
		//TODO FIX RETURN OK
		return "OK";
	}
}