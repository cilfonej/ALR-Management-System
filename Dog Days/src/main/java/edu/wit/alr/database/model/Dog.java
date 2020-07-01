package edu.wit.alr.database.model;

import java.sql.Blob;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import edu.wit.alr.database.model.roles.ApplicationCoordinator;
import edu.wit.alr.database.model.roles.Caretaker;

@Entity
public class Dog {
	public static enum LocationStatus {
		Shelter, Transport, Foster, Adopted;
	};
	
	public static enum Gender {
		Male, Female, Unknown;
		
		public static Gender parse(String name) {
			if(name.equalsIgnoreCase("male")) return Male;
			if(name.equalsIgnoreCase("female")) return Female;
			return Unknown;
		}
	};
	
	@Id
	@GenericGenerator(name = "dog_id_gen", strategy = "edu.wit.alr.database.util.IdGenerator")
	@GeneratedValue(generator = "dog_id_gen")  
	@Column(unique = true, nullable = false)
	private int id;
	
	@Column(length = 45, nullable = false)
	private String name;
	
	@Enumerated(EnumType.STRING)
	@Column
	private Gender gender;
	
	@Column
	private Integer dob_year;
	@Column
	private Integer dob_month;
	@Column
	private Integer dob_day;
	
	@Column(scale = 4, precision = 2)
	private Double weight;
	
	// TODO: last/next mediation date
	// Date-per-drug
	
	@Enumerated(EnumType.STRING)
	@Column
	private LocationStatus location;
	
	@Lob
	@Column
	private String description;
	
	@Column
	private Blob image;
	
	@ManyToOne
	@JoinColumn
	private Caretaker caretaker;
	
	@ManyToOne
	@JoinColumn
	private ApplicationCoordinator coordinator;
	
	// no-args constructor
	Dog() { }
	
	public Dog(String name) {
		this.name = name;
	}
	
	public LocalDate getBirthday() {
		return dob_year == null ? null : 
			LocalDate.of(
				dob_year, 
				dob_month == null ? 7 : dob_month, 
				dob_day == null ? 15 : dob_day
			); 
	}
	
	public int getAge() {
		return (int) getBirthday().until(LocalDate.now(), ChronoUnit.YEARS);
	}
	
	public Integer getDOB_Year() { return dob_year; }
	public Integer getDOB_Month() { return dob_month; }
	public Integer getDOB_Day() { return dob_day; }
	
	public int getID() { return id; }
	public String getName() { return name; }

	public Gender getGender() { return gender; }
	public Double getWeight() { return weight; }
	public LocationStatus getLocation() { return location; }
	
	public String getDescription() { return description; }
	public Blob getImage() { return image; }
	
	public Caretaker getCaretaker() { return caretaker; }
	public ApplicationCoordinator getAddoptionCoordinator() { return coordinator; }
	
	public void setBirthday(Integer year, Integer month, Integer day) { 
		this.dob_year = year; 
		this.dob_month = month; 
		this.dob_day = day; 
	}
	
	public void setGender(Gender gender) { 
		this.gender = gender;
	}
	
	public void setName(String name) { 
		this.name = name; 
	}
	
	public void setWeight(Double weight) { 
		this.weight = weight; 
	}
	
	public void setLocation(LocationStatus location) { 
		this.location = location; 
	}
	
	public void setDescription(String description) { 
		this.description = description; 
	}
	
	public void setImage(Blob image) { 
		this.image = image; 
	}
	
	public void setCaretaker(Caretaker caretaker) { 
		this.caretaker = caretaker; 
	}
	
	public void setAddoptionCoordinator(ApplicationCoordinator coordinator) {
		this.coordinator = coordinator;
	}
}
