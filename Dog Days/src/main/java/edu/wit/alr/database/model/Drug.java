package edu.wit.alr.database.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import edu.wit.alr.database.util.WeightRange;

@Entity
public class Drug {
	
	public static enum DrugType { 
		Heartworm("Heartworm"), Flea_Tic("Flea & Tick");
		
		private String name;
		DrugType(String displayName) {
			this.name = displayName;
		}
		
		public String getDisplayName() { return name; }
	}
	
	@Id
	@GeneratedValue
	@Column(unique = true, nullable = false)
	private int id;
	
	@Column(nullable = false, length = 128)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DrugType type;
	
	@Embedded
	private WeightRange weightRange;
	
	// no-args constructor
	Drug() { }
	
	public Drug(String name, DrugType type, double min, double max) {
		this.name = name;
		this.type = type;
		
		this.weightRange = new WeightRange(min, max);
	}
	
	public boolean suitableForWeight(double weight) {
		return weightRange.containWeight(weight);
	}

	public int getID() { return id; }
	
	public String getName() { return name; }
	public DrugType getType() { return type; }

	public WeightRange getWeightRange() { return weightRange; }

	public void setName(String name) {
		this.name = name;
	}

	public void setType(DrugType type) {
		this.type = type;
	}
}
