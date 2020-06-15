package edu.wit.alr.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.database.model.Address;
import edu.wit.alr.services.TransportService;

@Controller
@RequestMapping("/forms")
public class FormWebController {
	
	@Autowired
	private TransportService transportService;
	
	public static class TransportData {
		public String addressCity;  
		public String addressCountry; 
		public String addressState; 
		public String addressStreet; 
		public String addressZipcode; 
		
		//TODO DOGOBJECT NOT STRING
		public String chooseDog; 
		
		public String pickupDate; 
		public String pickupPerson; 
	}
	
	@PostMapping("/transport")
	public @ResponseBody String transportSubmit(@RequestBody TransportData data) {
		
		Address address = new Address(data.addressStreet, data.addressCity, data.addressState, data.addressZipcode, data.addressCountry); 
		//TODO Dog dog = 
				
		//TODO transportService.createTransport(address, dog, person, pickupDate);	
		//TODO FIX RETURN OK
		return "OK";
	}
	
}
