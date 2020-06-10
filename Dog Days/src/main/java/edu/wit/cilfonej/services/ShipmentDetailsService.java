package edu.wit.cilfonej.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.wit.cilfonej.database.model.Address;
import edu.wit.cilfonej.database.model.Distributor;
import edu.wit.cilfonej.database.model.Dog;
import edu.wit.cilfonej.database.model.Drug;
import edu.wit.cilfonej.database.model.Drug.DrugType;
import edu.wit.cilfonej.database.model.Foster;
import edu.wit.cilfonej.database.model.Person;
import edu.wit.cilfonej.database.model.Shipment;
import edu.wit.cilfonej.database.model.Shipment.Courier;
import edu.wit.cilfonej.database.model.Shipment.ShipmentStatus;
import edu.wit.cilfonej.database.model.UserRole;
import edu.wit.cilfonej.database.repository.DogRepository;
import edu.wit.cilfonej.database.repository.DrugRepository;
import edu.wit.cilfonej.database.repository.PersonRepository;
import edu.wit.cilfonej.database.repository.ShipmentRepository;
import edu.wit.cilfonej.services.session.UserSessionService;
import edu.wit.cilfonej.web.FilterInput;
import edu.wit.cilfonej.web.FilterInput.AddressFilterType;
import edu.wit.cilfonej.web.FilterInput.DogFilterType;
import edu.wit.cilfonej.web.FilterInput.DrugFilterType;
import edu.wit.cilfonej.web.FilterInput.FilterInputGroup;
import edu.wit.cilfonej.web.FilterInput.FostertFilterType;
import edu.wit.cilfonej.web.FilterInput.HomeAddressFilterType;
import edu.wit.cilfonej.web.FilterInput.MailingAddressFilterType;
import edu.wit.cilfonej.web.FilterInput.UserFilterType;
import edu.wit.cilfonej.web.response.ItemListInflator.ItemListInfo;
import edu.wit.cilfonej.web.response.ItemListInflator.ItemListInfo.LineItem;

@Service
public class ShipmentDetailsService {
	
	@Autowired 
	private UserSessionService session;
	
	@Autowired private DogRepository dogRepository;
	@Autowired private PersonRepository personRepository;
	
	@Autowired private ShipmentRepository shipmentRepository;
	@Autowired private DrugRepository drugRepository;

	@Transactional(readOnly = true)
	public Shipment initalizeShipmentInfo() {
		Shipment shipment = shipmentRepository.findAll().iterator().next();
		shipment.getItems().size();
		
		return shipment;
	}
	
	@Transactional(readOnly = true)
	public Shipment initalizeShipmentInfo(int id) {
		Shipment shipment = shipmentRepository.findById(id).get();
		shipment.getItems().size();
		
		return shipment;
	}
	
	@Transactional(readOnly = true)
	public Iterable<Shipment> listShipments(Dog dog) {
		ArrayList<Shipment> shipments = new ArrayList<>();
		shipmentRepository.findAllForDog(dog).forEach(shipments::add);
		
		shipments.sort((a, b) -> -a.getDateSent().compareTo(b.getDateSent()));
		return shipments;
	}
	
	public Shipment createNewShipment(Address address, int dog_id, String courier, String trackNum, ItemListInfo items) {
		Person from = session.resumeSession().getUser();
		Shipment shipment = new Shipment(from, address);
		
		if(trackNum != null) {
			shipment.setTrackingNumber(trackNum, Courier.byName(courier));
		}
		
		if(dog_id > 0) {
			// throws NoSuchElementException is no-such Dog
			shipment.setDog(dogRepository.findById(dog_id).get());
		}
		
		for(LineItem lineItem : items.getItems()) {
			shipment.addItem(lineItem.getItem(), lineItem.getCount());
		}
		
		shipment.setSender(from, LocalDate.now());
		shipment.setStatus(null, ShipmentStatus.Shipping);
		
		shipmentRepository.save(shipment);
		return shipment;
	}

	@Transactional(readOnly = true)
	public List<FilterInputGroup> listAddresses(int person_id) {
		Person person = personRepository.findById(person_id).get();
		
		List<FilterInputGroup> groups = new ArrayList<>();
		
		Set<Address> addresses = person.getAddresses();
		Set<Address> homeAddress = new HashSet<>();
		Set<Address> mailAddress = new HashSet<>();
		
		Foster foster = person.findRole(Foster.class);
		if(foster != null) {
			homeAddress.add(foster.getHomeAddress());
			mailAddress.add(foster.getMailingAddress());
		}
		
		Distributor distributor = person.findRole(Distributor.class);
		if(distributor != null) {
			mailAddress.add(distributor.getMailingAddress());
		}

		groups.add(new FilterInputGroup("Mailing Address", 1, mailAddress.stream()
				.filter(e -> e != null)
				.map(MailingAddressFilterType::new)
				.collect(Collectors.toList())));
		
		groups.add(new FilterInputGroup("Home Address", 2, homeAddress.stream()
				.filter(e -> e != null)
				.map(HomeAddressFilterType::new)
				.collect(Collectors.toList())));
		
		groups.add(new FilterInputGroup("Address", 3, addresses.stream()
				.filter(e -> e != null)
				.filter(address -> !homeAddress.contains(address) && !mailAddress.contains(address))
				.map(AddressFilterType::new)
				.collect(Collectors.toList())));
		
		return groups;
	}
	
	public List<FilterInputGroup> listForDog(Person person) {
		List<FilterInputGroup> groups = new ArrayList<>();
		
		Dog sessionDog = session.resumeSession().getDog();
		if(sessionDog != null) {
			FilterInputGroup recomended = new FilterInputGroup("Recommended", 1);
			recomended.addOption(new DogFilterType(sessionDog));
			groups.add(recomended);
		}
		
		if(person != null) {
			FilterInputGroup recomended = new FilterInputGroup("Fostering", 2);
			dogRepository.findAllByCustodian(person).forEach(dog -> recomended.addOption(new DogFilterType(dog)));
			groups.add(recomended);
		}

		List<FilterInput> list = new ArrayList<>();
		dogRepository.findAll().forEach(dog -> list.add(new DogFilterType(dog)));
		groups.add(new FilterInputGroup("All", 3, list));

		return groups;
	}
	
	public List<FilterInputGroup> listPersonTo(Dog forDog) {
		List<FilterInputGroup> groups = new ArrayList<>();
		
		if(forDog != null) {
			Person custodian = forDog.getCustodian();
			
			if(custodian != null) {
				Foster foster = custodian.findRole(Foster.class);
				groups.add(new FilterInputGroup("Recommended", 1)
						.addOption(foster != null ? new FostertFilterType(foster) : new UserFilterType(custodian)));
			}
		}

		List<FilterInput> list = new ArrayList<>();
		personRepository.findAll().forEach(person -> list.add(new UserFilterType(person)));
		groups.add(new FilterInputGroup("All", 2, list));

		return groups;
	}
	
	public List<FilterInputGroup> listDrugs(Dog forDog) {
		List<FilterInputGroup> groups = new ArrayList<>();
		
		Map<DrugType, Iterable<Drug>> drugs = new HashMap<>();
		for(DrugType type : DrugType.values()) {
			drugs.put(type, drugRepository.findAllByType(type));
		}
		
		if(forDog != null) {
			FilterInputGroup group = new FilterInputGroup("Recommended", 1);
			for(DrugType type : DrugType.values()) {
				for(Drug drug : drugs.get(type)) {
					// find 1-drug per type that's sutable for the dog
					if(drug.suitableForWeight(forDog.getWeight())) { 
						group.addOption(new DrugFilterType(drug));
						break;
					}
				}
			}
			
			groups.add(group);
		}

		for(DrugType type : DrugType.values()) {
			FilterInputGroup group = new FilterInputGroup(type.getDisplayName(), type.ordinal());
			for(Drug drug : drugs.get(type)) {
				group.addOption(new DrugFilterType(drug));
			}
			
			groups.add(group);
		}
		
		return groups;
	}
	
	@SafeVarargs
	public final Address pickAddress(Person person, Class<? extends UserRole>... targetRole) {
		for(Class<? extends UserRole> clazz : targetRole) {
			UserRole role = person.findRole(clazz);
			
			if(role != null) {
				if(clazz == Foster.class) {
					Foster foster = (Foster) role;
					if(foster.getMailingAddress() != null) return foster.getMailingAddress();
					if(foster.getHomeAddress() != null) return foster.getHomeAddress();
					
				} else if(clazz == Distributor.class) {
					Distributor distributor = (Distributor) role;
					if(distributor.getMailingAddress() != null) return distributor.getMailingAddress();
				
				} else {
					// TODO: log warning unsupported address role
				}
			}
		}
		
		Iterator<Address> iter = person.getAddresses().iterator();
		return iter.hasNext() ? iter.next() : null;
	}
}
