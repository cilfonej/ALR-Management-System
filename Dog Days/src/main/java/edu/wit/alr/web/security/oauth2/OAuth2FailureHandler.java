package edu.wit.alr.web.security.oauth2;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 	Callback function for when an OAuth2 Authorization request fails
 * 
 * 	@author cilfonej
 */

@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException {
        String targetUrl = "/";//repository.loadRedirectURI(req);

        e.getStackTrace();
        
        // TODO: yah, this all needs to change ._.
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", e.getLocalizedMessage())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(req, resp, targetUrl);
    }
}