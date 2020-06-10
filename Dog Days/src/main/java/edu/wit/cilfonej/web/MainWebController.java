package edu.wit.cilfonej.web;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.fasterxml.jackson.annotation.JsonAlias;

import edu.wit.cilfonej.Demo;
import edu.wit.cilfonej.database.model.Address;
import edu.wit.cilfonej.database.model.Dog;
import edu.wit.cilfonej.database.model.Drug;
import edu.wit.cilfonej.database.model.Foster;
import edu.wit.cilfonej.database.model.Person;
import edu.wit.cilfonej.database.model.Shipment;
import edu.wit.cilfonej.database.model.Shipment.ShipmentStatus;
import edu.wit.cilfonej.database.repository.AddressRepository;
import edu.wit.cilfonej.database.repository.DrugRepository;
import edu.wit.cilfonej.database.repository.PersonRepository;
import edu.wit.cilfonej.services.DogDetailsService;
import edu.wit.cilfonej.services.ShipmentDetailsService;
import edu.wit.cilfonej.web.FilterInput.FilterInputGroup;
import edu.wit.cilfonej.web.response.AddressDetails;
import edu.wit.cilfonej.web.response.AddressInflator;
import edu.wit.cilfonej.web.response.AddressInflator.AddressInfo;
import edu.wit.cilfonej.web.response.ItemListDetails;
import edu.wit.cilfonej.web.response.ItemListInflator;
import edu.wit.cilfonej.web.response.ItemListInflator.ItemListInfo;

@Controller
public class MainWebController {

	@Autowired private DrugRepository drugRepository;
	@Autowired private PersonRepository personRepository;
	@Autowired private AddressRepository addressRepository;
	
	@Autowired private TemplateEngine engine;
	
	@Autowired private AddressInflator addressInflator;
	@Autowired private ItemListInflator itemListInflator;
	
	@Autowired
	private DogDetailsService detailsService;

	@Autowired
	private ShipmentDetailsService shipmentDetailsService;
	
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
		
		Dog dog = detailsService.initalizeDogInfo();
		Person custodian = dog.getCustodian();
		Foster foster = custodian != null ? custodian.findRole(Foster.class) : null;
		
		view.addObject("dog", dog);
		view.addObject("foster", foster);
		
		view.addObject("history", shipmentDetailsService.listShipments(dog));
		
		return view;
	}
	
	@GetMapping("/render/address")
	public @ResponseBody String renderAddress(@RequestParam("id") int id) {
		Optional<Address> address = addressRepository.findById(id);
		if(address.isPresent()) {
			Context context = new Context();
			context.setVariable("addr", address.get());
			
			return engine.process("components/templates", Set.of("address"), context);
			
		} else {
			// TODO: build and return error message
			//			maybe lookup how to return error codes
		}
		
		return "";
	}
	
	@GetMapping("/render/shipment/new")
	public @ResponseBody String renderNewShipment() {
		// blank shipment, not saved as only used to populate template
		Shipment shipment = new Shipment();
		
		try {
			Thread.sleep(2000);
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Dog dog = detailsService.initalizeDogInfo();
		Person custodian = dog.getCustodian();

		if(custodian != null) {
			// lookup the best address to use for this person, given they're a foster
			Address address = shipmentDetailsService.pickAddress(custodian, Foster.class);
			if(address != null) shipment.setSendTo(address);
		}
		
		shipment.setDog(dog);
		shipment.setStatus(null, ShipmentStatus.Packing);
		
		// TODO: check dog and populate drugs
		
		Context context = new Context();
		context.setVariable("shipment", shipment);
		
		return engine.process("shipment/shipment", Set.of("sidebarContent"), context);
	}
	
	@GetMapping("/render/shipment/item")
	public @ResponseBody String renderShipmentItem(
			@RequestParam(name="drug_id") int drug_id, 
			@RequestParam(name="count") int count, 
			@RequestParam(name="readonly", defaultValue="false") boolean readonly) {
		
		Optional<Drug> drug = drugRepository.findById(drug_id);
		if(drug.isPresent()) {
			
			@SuppressWarnings("unused")
			class FakeLineItem {
				public Drug drug;
				public int quantity;
			}
			
			FakeLineItem item = new FakeLineItem();
			item.drug = drug.get();
			item.quantity = count;
			
			Context context = new Context();
			context.setVariable("item", item);
			context.setVariable("readonly", readonly);
			
			return engine.process("shipment/shipment", Set.of("item_row"), context);
			
		} else {
			// TODO: build and return error message
			//			maybe lookup how to return error codes
		}
		
		return "";
	}
	
	
	@GetMapping("/render/shipment/tab/new")
	public @ResponseBody String renderShipmentTab() {
		Context context = new Context();
		return engine.process("shipment/shipment", Set.of("shipment_tab"), context);
	}
	
	@GetMapping("/render/shipment/tab/receipt")
	public @ResponseBody String renderShipmentReceiptTab(@RequestParam(name="id") int shipment_id) {
		Context context = new Context();
		context.setVariable("shipment", shipmentDetailsService.initalizeShipmentInfo(shipment_id));
		return engine.process("shipment/shipment", Set.of("receipt_tab"), context);
	}
	
	@GetMapping("/render/shipment/history")
	public @ResponseBody String renderShipmentHistoryLine(@RequestParam(name="id") int shipment_id){
		Context context = new Context();
		context.setVariable("shipment", shipmentDetailsService.initalizeShipmentInfo(shipment_id));
		return engine.process("index/drug_history", Set.of("transactionRecord"), context);
	}
	

	private static class CreateShipmentRequest {
		@NonNull
		@JsonAlias("address") AddressDetails addressDetails;
		@JsonAlias("for_id") int for_dog_id = 0;
		
		@JsonAlias("courier_name") String courier = null;
		@JsonAlias("tracking_number") String tracking_number = null;

		@NonNull
		@JsonAlias("items") ItemListDetails itemDetails;

		public void setAddressDetails(AddressDetails addressDetails) {
			this.addressDetails = addressDetails;
		}

		public void setFor_dog_id(int for_dog_id) {
			this.for_dog_id = for_dog_id;
		}

		public void setCourier(String courier) {
			this.courier = courier;
		}

		public void setTracking_number(String tracking_number) {
			this.tracking_number = tracking_number;
		}

		public void setItemDetails(ItemListDetails itemDetails) {
			this.itemDetails = itemDetails;
		}
		
		
	}
	
	@PostMapping("/api/shipment/create")
	public @ResponseBody String createShipment(@RequestBody CreateShipmentRequest request) {
		
		AddressInfo addressInfo = addressInflator.inflate(request.addressDetails);
		ItemListInfo itemsInfo = itemListInflator.inflate(request.itemDetails);
		
		Shipment shipment = shipmentDetailsService.createNewShipment(
				addressInfo.getAddress(), request.for_dog_id, request.courier, request.tracking_number, itemsInfo);
		
		return shipment.getId() + "";
	}
	
	@RequestMapping(value = "/api/drug/info", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Drug getDrugInfo(@RequestParam(name="drug_id") int drug_id) {
		return drugRepository.findById(drug_id).orElse(null);
	}
	
	@RequestMapping(value = "/api/shipping/list/drugs", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<FilterInputGroup> listDrugs(@RequestParam(name="dog_id", defaultValue="0") int dog_id) {
		Dog dog = detailsService.getDogOrCurrent(dog_id);
		return shipmentDetailsService.listDrugs(dog);
	}
	
	@RequestMapping(value = "/api/shipping/list/addresses", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<FilterInputGroup> listAddresses(@RequestParam(name="person_id") int person_id) {
		return shipmentDetailsService.listAddresses(person_id);
	}
	
	@RequestMapping(value = "/api/shipping/list/to", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<FilterInputGroup> listShipTo(@RequestParam(name="dog_id", defaultValue="0") int dog_id) {
		Dog dog = detailsService.getDogOrCurrent(dog_id);
		return shipmentDetailsService.listPersonTo(dog);
	}
	
	@RequestMapping(value = "/api/shipping/list/for", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<FilterInputGroup> listShipFor(@RequestParam(name="person_id", defaultValue="0") int person_id) {
		Person person = personRepository.findById(person_id).orElse(null);
		return shipmentDetailsService.listForDog(person);
	}
}
