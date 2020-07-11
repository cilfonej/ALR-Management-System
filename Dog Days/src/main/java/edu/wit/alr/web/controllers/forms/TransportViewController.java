package edu.wit.alr.web.controllers.forms;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wit.alr.database.model.TransportReservation;
import edu.wit.alr.database.repository.TransportReservationRepository;
import edu.wit.alr.web.response.PageResponse;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller
@RequestMapping("/view/transport")
public class TransportViewController {
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@Autowired
	private ResponseBuilder builderService;
	
	@Autowired
	private TransportReservationRepository transRepo;
	
	@GetMapping("")
	public @ResponseBody String transportationUrl(@RequestParam(name="date", required=false) String date) {
		return builderService.buildIndependentPage(transportationRedirect(date));
	}
	
	@PostMapping("")
	public @ResponseBody PageResponse transportationRedirect(@RequestParam(name="date", required=false) String date) {

		// if no date was provided, find the closest transport to today
		if(date == null) {
			// TODO: find the closest transport date
		}
		
		//looks for yyyy-mm-dd DATE-FORMAT
		LocalDate sqlDate = LocalDate.parse(date, FORMATTER);
		
		Map<String, Map<String, List<TransportReservation>>> stateMap = new HashMap<>();
		
		for(TransportReservation tRes : transRepo.findAllByDate(sqlDate)) {
			String state =  tRes.getPickupAddress().getState();
			String city = tRes.getPickupAddress().getCity();
			String street = tRes.getPickupAddress().getStreetAddress();
			String cityStreet = street + ", " + city;
			
			Map<String, List<TransportReservation>> citystMap;
			
			if(stateMap.containsKey(state)) {
				citystMap = stateMap.get(state);
				
			} else {
				stateMap.put(state, new HashMap<>());
				citystMap = stateMap.get(state);
			}
			
			if(citystMap.containsKey(cityStreet)) {
				citystMap.get(cityStreet).add(tRes);
				
			} else {
				citystMap.put(cityStreet, new ArrayList<>());
				citystMap.get(cityStreet).add(tRes);
			}
		}
		
		Map<String, Object> vars = new HashMap<>();
		vars.put("state_map", stateMap);
		vars.put("date", sqlDate);
		
		return builderService.redirect("/view/transport?" + FORMATTER.format(sqlDate), "pages/transport/transportView :: page", vars);
	}
}

