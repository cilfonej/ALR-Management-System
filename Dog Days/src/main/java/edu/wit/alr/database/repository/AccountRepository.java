package edu.wit.alr.database.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import edu.wit.alr.database.model.Account;
import edu.wit.alr.web.security.AuthProviderType;

public interface AccountRepository extends CrudRepository<Account, Integer> {

	@Query("SELECT a FROM Account a WHERE a.username = ?1")
	public Optional<Account> findByUsername(String username);

	@Query("SELECT a FROM Account a WHERE a.external_id = ?1 AND a.auth_service = ?2")
	public Optional<Account> findByExternal(String external_id, AuthProviderType authority);
}
