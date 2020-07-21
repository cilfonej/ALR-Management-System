package edu.wit.alr.database.repository;

import org.springframework.data.repository.CrudRepository;

import edu.wit.alr.database.model.AuthorizedRedirect;

public interface AuthorizedRedirectRepository extends CrudRepository<AuthorizedRedirect, String> {

}
