package edu.wit.cilfonej.database.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Dog {

	@Id
	@GenericGenerator(name = "dog_id_gen", strategy = "edu.wit.cilfonej.database.util.IdGenerator")
	@GeneratedValue(generator = "dog_id_gen")  
	@Column(unique = true, nullable = false)
	private int id;
	
	@Column(length = 45, nullable = false)
	private String name;
	
	@Column
	private LocalDate date_of_birth;
	
	@Column(scale = 4, precision = 2)
	private double weight;
	
	@ManyToOne
	@JoinColumn
	private Person custody;
	
	// no-args constructor
	Dog() { }
	
	public Dog(String name) {
		this.name = name;
	}
	
	public int getAge() {
		return (int) date_of_birth.until(LocalDate.now(), ChronoUnit.YEARS);
	}
	
	public int getID() { return id; }
	public String getName() { return name; }
	
	public LocalDate getBirthday() { return date_of_birth; }
	public double getWeight() { return weight; }
	
	public Person getCustodian() { return custody; }
	
	public void setName(String name) { 
		this.name = name; 
	}
	
	public void setWeight(double weight) { 
		this.weight = weight; 
	}
	
	public void setBirthday(LocalDate birthday) { 
		this.date_of_birth = birthday; 
	}
	
	public void setCustodian(Person custody) { 
		this.custody = custody; 
	}
}
