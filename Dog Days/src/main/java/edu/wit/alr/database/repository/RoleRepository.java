package edu.wit.alr.database.repository;

import org.springframework.data.repository.CrudRepository;

import edu.wit.alr.database.model.roles.Role;

public interface RoleRepository extends CrudRepository<Role, Integer> {

}
