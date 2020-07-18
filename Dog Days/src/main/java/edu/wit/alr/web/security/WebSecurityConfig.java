package edu.wit.alr.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import edu.wit.alr.web.security.authentication.AuthenticationTokenFilter;
import edu.wit.alr.web.security.authentication.JWTAuthenticationProvider;
import edu.wit.alr.web.security.authentication.UnauthenticatedEntryPoint;
import edu.wit.alr.web.security.login.LoginFailureHandler;
import edu.wit.alr.web.security.login.LoginSuccessHandler;
import edu.wit.alr.web.security.login.oauth2.OAuth2UserProvider;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	public static final String[] RESOURCE_ANT_URIS = {
		"/favicon.ico", 
		"/webjars/**", 
		"/**/*.png", 
		"/**/*.gif", 
		"/**/*.svg", 
		"/**/*.jpg",
		"/**/*.css", 
		"/**/*.js",
		"/**/*.js.map"
	}; 

	@Autowired private AccountPrincipalService userDetailsService;
	@Autowired private JWTAuthenticationProvider jwtAuthProvider;
	
	@Autowired private OAuth2UserProvider oauthUserProvider;

	@Autowired private LoginSuccessHandler loginSuccessHandler;
	@Autowired private LoginFailureHandler loginFailureHandler;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors()
				.and()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
				.and()
			.csrf()
				.disable()
			.formLogin()
				.disable()
			.httpBasic()
				.disable()
			.exceptionHandling()
				.authenticationEntryPoint(new UnauthenticatedEntryPoint())
				.and()
			.authorizeRequests()
				// resources
				.antMatchers(RESOURCE_ANT_URIS)
					.permitAll()
				// public login
				.antMatchers("/login/**", "/error")
					.permitAll()
				// oauth2 login/callbacks
				.antMatchers("/auth/**", "/oauth2/**")
					.permitAll()
				// authorized-redirects
				.antMatchers("/r/**")
					.permitAll()
				// TODO: remove
				.antMatchers("/gen_data")
					.permitAll()
				// everything else
				.anyRequest()
					.authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.loginProcessingUrl("/login/local")
					.usernameParameter("username")
					.passwordParameter("password")
				.successHandler(loginSuccessHandler)
				.failureHandler(loginFailureHandler)
				.and()
			.oauth2Login()
				.authorizationEndpoint()
					.baseUri("/oauth2/authorize")
//					.authorizationRequestRepository(requestRepository)
					.and()
				.redirectionEndpoint()
					.baseUri("/oauth2/callback/*")
					.and()
				.userInfoEndpoint()
					.userService(oauthUserProvider)
					.and()
				.successHandler(loginSuccessHandler)
				.failureHandler(loginFailureHandler)
				.and()
			.logout()
				.logoutUrl("/logout") //the URL on which the clients should post if they want to logout
//				.logoutSuccessHandler(null) // TODO
				.invalidateHttpSession(true);

		// Add our custom Token based authentication filter
		http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder builder) throws Exception {
		builder
			.authenticationProvider(daoAuthenticationProvider())
			.authenticationProvider(jwtAuthProvider)
			.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder());
	}
	
	@Bean
	public AuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(userDetailsService);
		return provider;
	}
	
	@Bean
	public AuthenticationTokenFilter tokenAuthenticationFilter() {
		return new AuthenticationTokenFilter();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		int saltLength = 32; // salt length in bytes
		int hashLength = 64; // hash length in bytes

		int iterations = 8;
		int memory = 512000; // memory costs 4096
		int parallelism = 1; // currently not supported by Spring Security

		return new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
	}
}
