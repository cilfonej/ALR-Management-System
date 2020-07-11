package edu.wit.alr.web.controllers.pages;

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
import edu.wit.alr.services.TransportService;
import edu.wit.alr.web.response.PageResponse;
import edu.wit.alr.web.response.ResponseBuilder;

@Controller
@RequestMapping("/view/transport")
public class TransportViewController {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@Autowired
	private ResponseBuilder builder;
	
	@Autowired
	private TransportService service;
	
	@GetMapping("")
	public @ResponseBody String transportationUrl(@RequestParam(name="date", required=false) String date) {
		return builder.buildIndependentPage(transportationRedirect(date));
	}
	
	@PostMapping("")
	public @ResponseBody PageResponse transportationRedirect(@RequestParam(name="date", required=false) String date) {
		LocalDate sqlDate, nextDate, prevDate;
		
		// if no date value was provided, find the closest transport to today
		if(date == null) {
			LocalDate[] dates = service.getClosestDateRange(LocalDate.now());
			
			prevDate = dates[0];
			sqlDate = dates[1];
			nextDate = dates[2];
			
		} else {
			// looks for yyyy-mm-dd DATE-FORMAT
			LocalDate providedDate = LocalDate.parse(date, FORMATTER);
			
			// look for the neighboring transport dates for the date provided 
			LocalDate[] dates = service.getClosestDateRange(providedDate);
			
			prevDate = dates[0];
			sqlDate = providedDate; // still use the provided date (could be no data)
			nextDate = dates[2];
		}
		
		
		Map<String, Map<String, List<TransportReservation>>> stateMap = new HashMap<>();
		
		// go through all the reservation for the selected date (could be none)
		for(TransportReservation tRes : service.getReservations(sqlDate)) {
			String state =  tRes.getPickupAddress().getState();
			String city = tRes.getPickupAddress().getCity();
			String street = tRes.getPickupAddress().getStreetAddress();
			String cityStreet = street + ", " + city;
			
			Map<String, List<TransportReservation>> citystMap;
			
			// sort by "state"
			if(stateMap.containsKey(state)) {
				citystMap = stateMap.get(state);
				
			} else {
				stateMap.put(state, new HashMap<>());
				citystMap = stateMap.get(state);
			}
			
			// sort by "street, city"
			if(citystMap.containsKey(cityStreet)) {
				citystMap.get(cityStreet).add(tRes);
				
			} else {
				citystMap.put(cityStreet, new ArrayList<>());
				citystMap.get(cityStreet).add(tRes);
			}
		}
		
		Map<String, Object> vars = new HashMap<>();
		vars.put("state_map", stateMap);
		
		vars.put("prev_date", prevDate);
		vars.put("date", sqlDate);
		vars.put("next_date", nextDate);
		
		return builder.redirect("/view/transport?date=" + FORMATTER.format(sqlDate), "pages/transport/transportView :: page", vars);
	}
}

