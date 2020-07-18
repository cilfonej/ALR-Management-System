package edu.wit.alr.web.security.authentication;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import edu.wit.alr.web.security.UserPrincipal;

/**
 * 	Entry-Point responsible for redirecting the user when they fail an authentication check. <br/>
 * 	There are two cases in which this can occur:
 * 	<ul>
 * 		<li>
 * 			<h2> No-Authorization Found </h2>
 * 			<p> If the user is missing any form of authorization, then they are redirected to the login page. </p>
 * 		</li>
 * 		<li>
 * 			<h2> Insufficient Authorization </h2>
 * 			<p> If the user has an authorization, but lacks permission to the resource, an error is show
 * 				with a link to the login page. </p>
 * 		</li>
 * 	</ul>
 * 
 *	@author cilfonej
 */

@Component
public class UnauthenticatedEntryPoint implements AuthenticationEntryPoint {
	@Autowired
	private SessionSecurityService sessionService;
	
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
		// get the current authentication
		Authentication principal = SecurityContextHolder.getContext().getAuthentication();
		
		sessionService.recordRedirectLink(request.getRequestURI());
		
		// if the principal is null, or not a UserPrinciple
		if(principal == null || !(principal instanceof UserPrincipal)) {
			// then send user to login page
			response.sendRedirect("/login");
		
		} else {
			// Insufficient permission
			// TODO: notify user of lack of permissions and offer link to login page, or back-button
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getLocalizedMessage());
		}
	}
}