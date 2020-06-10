package edu.wit.cilfonej.database.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import edu.wit.cilfonej.database.model.Drug;
import edu.wit.cilfonej.database.model.Drug.DrugType;

public interface DrugRepository extends CrudRepository<Drug, Integer> {

	@Query("SELECT d FROM Drug d WHERE type = ?1")
	public Iterable<Drug> findAllByType(DrugType type);
}
