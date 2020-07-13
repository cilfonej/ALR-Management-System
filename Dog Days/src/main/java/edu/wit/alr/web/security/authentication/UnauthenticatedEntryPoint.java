package edu.wit.alr.web.security.authentication;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * 	Entry-Point responsible for generating the response when a user fails an Authentication check.
 * 
 *	@author cilfonej
 */
public class UnauthenticatedEntryPoint implements AuthenticationEntryPoint {

	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
		// TODO: Log unauthorized entry attempt
//		logger.error("Responding with unauthorized error. Message - {}", e.getMessage());
		
		// TODO: better response then this -_-
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getLocalizedMessage());
	}
}