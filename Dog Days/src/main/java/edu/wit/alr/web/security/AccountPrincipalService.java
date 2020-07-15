package edu.wit.alr.web.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.wit.alr.database.model.Account;
import edu.wit.alr.database.model.roles.Role;
import edu.wit.alr.database.repository.AccountRepository;

/**
 *	Service responsible for loading an {@link Account} from the Database and converting it into
 *	a {@link UserPrincipal}.
 *
 *	@see UserDetails
 *
 * 	@author cilfonej
 */
@Service
public class AccountPrincipalService implements UserDetailsService {

	@Autowired
	private AccountRepository repository;
	
	public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = repository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("No such Account with username: " + username));
		
		if(account.getAuthService() != AuthProviderType.local)
			throw new IllegalArgumentException("Cannot lookup an external account via a Username");

		return loadFromAccount(account);
	}

	public UserPrincipal loadUserByExternalId(String external_id, AuthProviderType authority) throws UsernameNotFoundException {
		Account account = repository.findByExternal(external_id, authority)
				.orElseThrow(() -> new UsernameNotFoundException("No such Account with external-id: " + external_id));
		
		if(account.getAuthService() == AuthProviderType.local)
			throw new IllegalArgumentException("Cannot lookup an local account via an external-id");

		return loadFromAccount(account);
	}
	
	public UserPrincipal loadUserByAccountId(long id) throws UsernameNotFoundException {
		Account account = repository.findById((int) id)
				.orElseThrow(() -> new UsernameNotFoundException("No such Account with id: " + id));
		
		return loadFromAccount(account);
	}
	
	public UserPrincipal loadFromAccount(Account account) {
		// TODO: Replace after roles no longer need @Transactional
		Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
//															account.getPerson().getRoles().stream()
//																.map(role -> role.getClass().getSimpleName())
//																.map(SimpleGrantedAuthority::new)
//																.collect(Collectors.toSet());
	
		if(account.getAuthService() == AuthProviderType.local)
			return new UserPrincipal(account.getId(), account.getUsername(), account.getPassword().getHash(), authorities);
		else
			return new UserPrincipal(account.getId(), String.valueOf(account.getExternalId()), null, authorities);
	}
	
	public Account loadAccountByUsername(String username) {
		return repository.findByUsername(username).orElse(null);
	}
	
	public Account loadAccountByExternalId(String external_id, AuthProviderType authority) {
		return repository.findByExternal(external_id, authority).orElse(null);
	}
	
	@SafeVarargs
	public static String[] asRoles(Class<? extends Role>... roles) {
		return Arrays.stream(roles).map(c -> c.getSimpleName()).toArray(size -> new String[size]);
	}
}
