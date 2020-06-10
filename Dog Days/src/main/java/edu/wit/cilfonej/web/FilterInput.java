package edu.wit.cilfonej.web;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.wit.cilfonej.database.model.Address;
import edu.wit.cilfonej.database.model.Distributor;
import edu.wit.cilfonej.database.model.Dog;
import edu.wit.cilfonej.database.model.Drug;
import edu.wit.cilfonej.database.model.Foster;
import edu.wit.cilfonej.database.model.Person;

public abstract class FilterInput {
	private String key;
	private String text;
	private String value;
	private String filterText;

	public FilterInput(String key, String value, String text, String filterText) {
		this.key = key;
		this.text = text;
		this.value = value;
		this.filterText = filterText;
	}

	public abstract String getType();
	
	public String getKey() { return key; }
	public String getText() { return text; }
	public String getValue() { return value; }
	public String getFilter_text() { return filterText; }

	public static class FilterInputGroup {
		private int order;
		private String name;
		private List<FilterInput> options;
		
		private FilterInputGroup() { }
		
		public FilterInputGroup(String name, int order) {
			this(name, order, new ArrayList<>());
		}
		
		public FilterInputGroup(String name) {
			this(name, 0);
		}
		
		public FilterInputGroup(String name, int order, List<FilterInput> options) {
			this.name = name;
			this.order = order;
			this.options = options;
		}
		
		public FilterInputGroup addOption(FilterInput option) { 
			options.add(option); 
			return this; 
		}
		
		public static List<FilterInputGroup> fromMap(Map<String, List<FilterInput>> groupings) {
			List<FilterInputGroup> list = new ArrayList<>();
			
			for(Map.Entry<String, List<FilterInput>> groupEntry : groupings.entrySet()) {
				FilterInputGroup group = new FilterInputGroup();
				group.name = groupEntry.getKey();
				group.options = groupEntry.getValue();
				
				list.add(group);
			}
			
			list.sort((a, b) -> a.order - b.order);
			return list;
		}

		public int getOrder() { return order; }
		public String getName() { return name; }
		public List<FilterInput> getOptions() { return options; }
	}
	
	public static class UserFilterType extends FilterInput {
		private Person person;
		
		public UserFilterType(Person person) {
			super(String.valueOf(person.getID()), String.valueOf(person.getID()), person.getName(), person.getName());
			this.person = person;
		}

		public String getName() { return person.getName(); }
		public String getType() { return "user"; }
	}
	
	public static class FostertFilterType extends UserFilterType {
		public FostertFilterType(Foster foster) {
			super(foster.getBasePerson());
		}

		public String getType() { return "foster"; }
	}
	
	public static class DistributorFilterType extends UserFilterType {
		public DistributorFilterType(Distributor distributor) {
			super(distributor.getBasePerson());
		}

		public String getType() { return "distributor"; }
	}
	
	public static class DogFilterType extends FilterInput {
		private Dog dog;
		
		public DogFilterType(Dog dog) {
			super(String.valueOf(dog.getID()), String.valueOf(dog.getID()), dog.getName(), dog.getName() + dog.getID());
			this.dog = dog;
		}

		public int getNumber() { return dog.getID(); }		
		public String getName() { return dog.getName(); }

		public String getType() { return "dog"; }
	}
	
	public static class AddressFilterType extends FilterInput {
		private Address address;

		public AddressFilterType(Address address) {
			this(address, "Address");
		}
		
		public AddressFilterType(Address address, String name) {
			super(String.valueOf(address.getID()), String.valueOf(address.getID()), 
					formatSearchString(address, true), 
					name + " " + formatSearchString(address, false));
			this.address = address;
		} 
		
		public static String formatSearchString(Address address, boolean display) {
			if(address == null) return "";
			
			StringBuilder builder = new StringBuilder();
			builder.append(address.getStreetAddress());
			builder.append(display ? "\n" : " ");
			
			if(address.getCity() != null) 
				builder.append(address.getCity());
			if(address.getCity() != null && address.getState() != null) 
				builder.append(", ");
			if(address.getState() != null) 
				builder.append(address.getState());
			
			builder.append(display ? "\n" : " ");
			
			if(address.getPostalCode() != null) 
				builder.append(address.getPostalCode());
			
			return builder.toString();
		}

		public String getStreetAddress() { return address.getStreetAddress(); }
		public String getCity() { return address.getCity(); }
		public String getState() { return address.getState(); }
		public String getPostalCode() { return address.getPostalCode(); }
		
		public String getType() { return "address"; }
	}
	
	public static class HomeAddressFilterType extends AddressFilterType {
		public HomeAddressFilterType(Address address) {
			super(address, "Home Address");
		}

		public String getType() { return "home_address"; }
	}
	
	public static class MailingAddressFilterType extends AddressFilterType {
		public MailingAddressFilterType(Address address) {
			super(address, "Mailing Address");
		}

		public String getType() { return "mailing_address"; }
	}
	
	public static class DrugFilterType extends FilterInput {
		private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
		private Drug drug;
		
		public DrugFilterType(Drug drug) {
			super(String.valueOf(drug.getID()), String.valueOf(drug.getID()), drug.getName(), 
					drug.getType().getDisplayName() + " " + drug.getName() + " " + 
							FORMAT.format(drug.getWeightRange().getMin()) + "-" + 
							FORMAT.format(drug.getWeightRange().getMax()) + "lbs");
			this.drug = drug;
		}

		public String getName() { return drug.getName(); }
		public String getMin() { return FORMAT.format(drug.getWeightRange().getMin()); }
		public String getMax() { return FORMAT.format(drug.getWeightRange().getMax()); }
		
		public String getType() { return "drug"; }
	}
}
