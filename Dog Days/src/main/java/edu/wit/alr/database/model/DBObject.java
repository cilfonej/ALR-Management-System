package edu.wit.alr.database.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
public class DBObject {
	@Id
	@GenericGenerator(name = "id_gen", strategy = "edu.wit.alr.database.util.SckIdGenerator")
	@GeneratedValue(generator = "id_gen")  
	@Column(unique = true, nullable = false)
	private int id;
	
	DBObject() { }
	
	public int getId() { return id; }
}
