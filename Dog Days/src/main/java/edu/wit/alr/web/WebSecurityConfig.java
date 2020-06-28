package edu.wit.alr.web;

import java.util.Arrays;
import java.util.function.IntFunction;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import edu.wit.alr.database.model.roles.Role;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll();
	}
	
	@SafeVarargs
	public static String[] asRoles(Class<? extends Role>... roles) {
		return Arrays.stream(roles).map(c -> c.getSimpleName()).toArray(size -> new String[size]);
	}
}
