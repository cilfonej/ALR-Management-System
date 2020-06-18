package edu.wit.alr.web.lookups;

import edu.wit.alr.database.model.Address;

public class AddressLookupOption extends LookupOption {
	protected String streetAddress;
	protected String city;
	protected String state;
	protected String postalCode;
	
	protected int id;

	// no-args constructor
	AddressLookupOption() { }
	
	public AddressLookupOption(Address address) {
		this(address, "Address");
	}

	public AddressLookupOption(Address address, String name) {
		this("address", address, name);
	}
	
	public AddressLookupOption(String type, Address address, String name) {
		super(type, 
				name + " " + formatSearchString(address, false), 
				formatSearchString(address, true), 
				address.getID());
		
		this.streetAddress = address.getStreetAddress();
		this.city = address.getCity();                  
		this.state = address.getState();                
		this.postalCode = address.getPostalCode();   
		
		this.id = address.getID();
	} 
	
	public int getID() { return id; }
	
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

	public static class HomeAddressLookupOption extends AddressLookupOption {
		public HomeAddressLookupOption(Address address) {
			super("home_address", address, "Home Address");
		}
	}
	
	public static class MailingAddressLookupOption extends AddressLookupOption {
		public MailingAddressLookupOption(Address address) {
			super("mailing_address", address, "Mailing Address");
		}
	}
}
