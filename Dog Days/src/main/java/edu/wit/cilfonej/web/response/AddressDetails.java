package edu.wit.cilfonej.web.response;

public class AddressDetails {
	protected int address_id;
	
	protected int person_id;
	protected String streetName;
	protected String city, state;
	protected String postalCode;
	
	public void setAddress_id(int address_id) {
		this.address_id = address_id;
	}
	public void setPerson_id(int person_id) {
		this.person_id = person_id;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public void setState(String state) {
		this.state = state;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	
}
