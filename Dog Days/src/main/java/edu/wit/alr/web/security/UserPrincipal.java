package edu.wit.alr.web.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import edu.wit.alr.database.model.Account;

public class UserPrincipal implements OAuth2User, UserDetails {
	private static final long serialVersionUID = 2024067849159492889L;
	
	// account-id
	private long id;
	private String name;
	
	private String email;
	private String password;
	
	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, Object> attributes;

	UserPrincipal(Account account, String email, String password, Collection<? extends GrantedAuthority> authorities) {
		this.id = account.getId();
		this.name = account.getPerson().getName();
		
		this.email = email;
		this.password = password;
		this.authorities = authorities;
	}

	public long getId() { return id; }
	public String getName() { return name; }

	public String getEmail() { return email; }
	public String getUsername() { return email; }
	public String getPassword() { return password; }

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	public boolean isEnabled() { return true; }
	public boolean isAccountNonLocked() { return true; }
	public boolean isAccountNonExpired() { return true; }
	public boolean isCredentialsNonExpired() { return true; }

}