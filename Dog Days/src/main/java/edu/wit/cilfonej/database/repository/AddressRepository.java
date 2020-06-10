package edu.wit.cilfonej.database.repository;

import org.springframework.data.repository.CrudRepository;

import edu.wit.cilfonej.database.model.Address;

public interface AddressRepository extends CrudRepository<Address, Integer> {

}
