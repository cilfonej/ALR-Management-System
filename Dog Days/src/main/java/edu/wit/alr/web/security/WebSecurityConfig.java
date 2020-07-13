package edu.wit.alr.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import edu.wit.alr.web.security.authentication.AuthenticationTokenFilter;
import edu.wit.alr.web.security.authentication.UnauthenticatedEntryPoint;
import edu.wit.alr.web.security.oauth2.OAuth2FailureHandler;
import edu.wit.alr.web.security.oauth2.OAuth2SuccessHandler;
import edu.wit.alr.web.security.oauth2.OAuth2UserProvider;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired private AccountDetailsService userDetailsService;
	@Autowired private OAuth2UserProvider oauthUserProvider;

	@Autowired private OAuth2SuccessHandler oauth2SuccessHandler;
	@Autowired private OAuth2FailureHandler oauth2FailureHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors()
				.and()
//			.sessionManagement()
//				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//				.and()
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
				.antMatchers(
						"/", 
						"/error", 
						"/favicon.ico", 
						"/**/*.png", 
						"/**/*.gif", 
						"/**/*.svg", 
						"/**/*.jpg",
						"/**/*.html", 
						"/**/*.css", 
						"/**/*.js")
					.permitAll()
				.antMatchers("/auth/**", "/oauth2/**")
					.permitAll()
				.anyRequest()
					.authenticated()
				.and()
			.oauth2Login()
				.authorizationEndpoint()
					.baseUri("/oauth2/authorize")
//					.authorizationRequestRepository(cookieAuthorizationRequestRepository())
					.and()
				.redirectionEndpoint()
					.baseUri("/oauth2/callback/*")
					.and()
				.userInfoEndpoint()
					.userService(oauthUserProvider)
					.and()
				.successHandler(oauth2SuccessHandler)
				.failureHandler(oauth2FailureHandler);

		// Add our custom Token based authentication filter
		http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder builder) throws Exception {
		builder
			.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder());
	}
	
	@Override
	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public AuthenticationTokenFilter tokenAuthenticationFilter() {
		return new AuthenticationTokenFilter();
	}

//	@Bean
//	public HttpCookieOAuth2RequestRepository cookieAuthorizationRequestRepository() {
//		return new HttpCookieOAuth2RequestRepository();
//	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
