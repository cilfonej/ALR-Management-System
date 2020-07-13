package edu.wit.alr.web.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

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
public class AccountDetailsService implements UserDetailsService {

	@Autowired
	private AccountRepository repository;
	
	public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = repository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("No such Account with username: " + username));
		
		if(account.getAuthService() != AuthProviderType.local)
			throw new IllegalArgumentException("Cannot lookup an external account via a Username");

		Collection<? extends GrantedAuthority> authorities = account.getPerson().getRoles().stream()
																.map(role -> role.getClass().getSimpleName())
																.map(SimpleGrantedAuthority::new)
																.collect(Collectors.toSet());
		
		return new UserPrincipal(account.getId(), account.getUsername(), account.getPassword().getHash(), authorities);
	}
	
	public UserPrincipal loadUserByAccountId(long id) throws UsernameNotFoundException {
		Account account = repository.findById((int) id)
				.orElseThrow(() -> new UsernameNotFoundException("No such Account with id: " + id));

		Collection<? extends GrantedAuthority> authorities = account.getPerson().getRoles().stream()
																.map(role -> role.getClass().getSimpleName())
																.map(SimpleGrantedAuthority::new)
																.collect(Collectors.toSet());
		
		return new UserPrincipal(account.getId(), account.getUsername(), account.getPassword().getHash(), authorities);
	}
	
	public UserPrincipal loadUserByExternalId(String external_id) throws UsernameNotFoundException {
		Account account = repository.findByExternal(external_id)
				.orElseThrow(() -> new UsernameNotFoundException("No such Account with external-id: " + external_id));
		
		if(account.getAuthService() == AuthProviderType.local)
			throw new IllegalArgumentException("Cannot lookup an local account via an external-id");
		
		Collection<? extends GrantedAuthority> authorities = account.getPerson().getRoles().stream()
																.map(role -> role.getClass().getSimpleName())
																.map(SimpleGrantedAuthority::new)
																.collect(Collectors.toSet());
		
		return new UserPrincipal(account.getId(), String.valueOf(account.getExternalId()), null, authorities);
	}
	
	@SafeVarargs
	public static String[] asRoles(Class<? extends Role>... roles) {
		return Arrays.stream(roles).map(c -> c.getSimpleName()).toArray(size -> new String[size]);
	}
}
