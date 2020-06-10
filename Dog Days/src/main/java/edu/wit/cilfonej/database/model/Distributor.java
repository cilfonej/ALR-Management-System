package edu.wit.cilfonej.database.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
public class Distributor extends UserRole {

	@OneToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false)
	private Address mailing_address;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<DrugSupply> items;
	
	// no-args constructor
	Distributor() {
		this.items = new HashSet<>();
	}
	
	public Distributor(String firstname, String lastname, Address mailAddress) {
		this(new Person(firstname, lastname), mailAddress);
	}
	
	public Distributor(Person person, Address mailingAddress) {
		super(person);
		this.items = new HashSet<>();
		setMailingAddress(mailingAddress);
	}

	public Address getMailingAddress() { return mailing_address; }
	public Set<DrugSupply> getItems() { return items; }

	public void setMailingAddress(Address address) {
		super.addAddress(address);
		this.mailing_address = address;
	}

	public DrugSupply addItem(Drug drug, int amount) {
		DrugSupply item = new DrugSupply(this, drug, amount);
		items.add(item);
		return item;
	}
	
	public boolean removeItem(DrugSupply item) {
		return this.items.remove(item);
	}
	
//	======================================== ====== ======================================== \\
//	======================================== Supply ======================================== \\
	
	@Entity
	@Table(name = "Supply")
	public static class DrugSupply {
		@EmbeddedId
		private Key id;
		
		@Column
		private int amount;
		
		@Embeddable
		private static class Key implements Serializable {
			private static final long serialVersionUID = -5096280726294280430L;

			@ManyToOne(optional = false, fetch = FetchType.LAZY)
			@JoinColumn(nullable = false)
			private Distributor manager;

			@ManyToOne(optional = false)
			@JoinColumn(nullable = false)
			private Drug drug;
		}
		
		// no-args constructor
		DrugSupply() { }
		
		public DrugSupply(Distributor manager, Drug drug, int amount) {
			this.id = new Key();
			id.manager = manager;
			id.drug = drug;
			
			this.amount = amount;
		}

		public Drug getDrug() { return id.drug; }
		public int getAmount() { return amount; }

		public void setAmount(int amount) {
			this.amount = amount;
		}
	}
}
