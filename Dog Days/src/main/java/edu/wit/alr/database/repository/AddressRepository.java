package edu.wit.alr.database.repository;

import org.springframework.data.repository.CrudRepository;

import edu.wit.alr.database.model.Address;

public interface AddressRepository extends CrudRepository<Address, Integer> {

}
