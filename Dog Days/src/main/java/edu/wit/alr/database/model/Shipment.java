package edu.wit.alr.database.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.lang.NonNull;

@Entity
public class Shipment {
	
	public static enum Courier { 
		USPS, UPS, FedEx, DHL, Other;
		
		public static Courier byName(String name) {
			for(Courier courier : values()) {
				if(courier.name().equalsIgnoreCase(name)) {
					return courier;
				}
			}
			
			return Other;
		}
	}

	public static enum ShipmentStatus { Packing, Shipping, Delivered, Opened }
	
	@Id
	@GeneratedValue
	@Column(unique = true, nullable = false)
	private int id;

	@ManyToOne
	@JoinColumn
	private Person from;

	@ManyToOne(optional = false)
	@JoinColumn(nullable = false)
	private Address to;
	
	@ManyToOne
	@JoinColumn
	private Dog for_dog;
	
	@Column(nullable = false)
	private LocalDate date_sent;
	
	@Column
	private LocalDate date_recv;
	
	@Column(length = 48)
	private String trackingNumber;
	
	@Enumerated(EnumType.STRING)
	@Column
	private Courier courier;
	
	@Column
	private LocalDateTime lastUpdated;
	
	@Enumerated(EnumType.STRING)
	@Column
	private ShipmentStatus status;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Item> items;
	
	// no-args constructor
	public Shipment() {
		items = new HashSet<>();
	}
	
	public Shipment(Person from, @NonNull Address to) {
		this();
		
		this.from = from;
		this.to = to;
		
		this.status = ShipmentStatus.Packing;
	}
	
	public int getId() { return id; }

	public Dog getFor() { return for_dog; }
	public Person getFrom() { return from; }
	public Address getTo() { return to; }

	public String getTrackingNumber() { return trackingNumber; }
	public Courier getCourier() { return courier; }

	public ShipmentStatus getStatus() { return status; }
	
	public LocalDate getDateSent() { return date_sent; }
	public LocalDate getDateRecvied() { return date_recv; }
	public LocalDateTime getLastUpdated() { return lastUpdated; }
	
	public Set<Item> getItems() { return items; }

	public void setDog(Dog dog) {
		this.for_dog = dog;
	}
	
	public void setSendTo(Address address) {
		this.to = address;
	}

	public void setSender(Person person, LocalDate sentDate) {
		this.from = person;
		this.date_sent = sentDate;
	}
	
	public void setStatus(LocalDate recvDate, @NonNull ShipmentStatus status) {
		if(status == ShipmentStatus.Opened && recvDate == null)
			throw new IllegalArgumentException("Must provide a date when package was opened");
			
		this.status = status;
		this.date_recv = recvDate;
		
		this.lastUpdated = LocalDateTime.now();
	}
	
	public void setTrackingNumber(@NonNull String trackingNumber, Courier courier) {
		this.trackingNumber = trackingNumber;
		this.courier = courier;
	}
	
	public Item addItem(Drug drug, int amount) {
		Item item = new Item(this, drug, amount);
		items.add(item);
		return item;
	}
	
	public boolean removeItem(Item item) {
		return this.items.remove(item);
	}

//	======================================== ============= ======================================== \\
//	======================================== Shipment Item ======================================== \\
	
	@Entity
	@Table(name = "Shipment_Item")
	public static class Item {
		@EmbeddedId
		private Key id;
		
		@Column
		private int quantity;
		
		@Embeddable
		private static class Key implements Serializable {
			private static final long serialVersionUID = -8509117711834135649L;

			@ManyToOne(optional = false, fetch = FetchType.LAZY)
			@JoinColumn(nullable = false)
			private Shipment shipment;

			@ManyToOne(optional = false)
			@JoinColumn(nullable = false)
			private Drug drug;
		}
		
		// no-args constructor
		Item() { }
		
		public Item(Shipment shipment, Drug drug, int quantity) {
			this.id = new Key();
			id.shipment = shipment;
			id.drug = drug;
			
			this.quantity = quantity;
		}

		public Drug getDrug() { return id.drug; }
		public int getQuantity() { return quantity; }

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
	}
}
