package edu.wit.alr.web.security.login.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import edu.wit.alr.database.model.Account;
import edu.wit.alr.database.model.AuthorizedRedirect;
import edu.wit.alr.services.SignupService;
import edu.wit.alr.web.security.AccountPrincipalService;
import edu.wit.alr.web.security.AuthProviderType;
import edu.wit.alr.web.security.login.LoginController;
import edu.wit.alr.web.security.login.oauth2.userinfo.OAuth2UserInfo;
import edu.wit.alr.web.security.login.oauth2.userinfo.OAuth2UserInfoFactory;

/**
 * 	Service used for converting an {@link OAuth2UserRequest Authentication-Request} into
 * 	a {@link AuthenticatedPrincipal User} to be used by Spring's authentication/security system.
 * 	
 * 	<p>
 * 	This Service is also responsible for finalizing the {@link Account} configuration for a person
 * 	who wishes to log-in via an external OAuth2 service. An {@link Account} must have already be loaded 
 * 	into the current session for this to be achieved. This is most often done by the 
 * 	{@link LoginController#signupPage(HttpServletRequest)} via following an 
 * 	"sign-up invitation" {@link AuthorizedRedirect} link.
 * 
 * 	@author cilfonej
 */

@Service
public class OAuth2UserProvider extends DefaultOAuth2UserService {

	@Autowired
	private AccountPrincipalService principalService;

	@Autowired
	private SignupService signupService;

	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		try {
			// request the default OAuth2User from the parent-service
			OAuth2User user = super.loadUser(userRequest);
			
			// use that user's data, and the request, to configure the account
			// this will also produce a custom AuthenticatedPrincipal for Spring
			return updateAccountConfig(userRequest, user);
			
		// AuthenticationExceptions auto-trigger the OAuth2AuthenticationFailureHandler
		} catch(AuthenticationException e) {
			throw e;
			
		} catch(Exception e) {
			// if any other kind of exception occurred, throw a new AuthenticationExceptions
			throw new InternalAuthenticationServiceException("Failed to create User", e);
		}
	}

	private OAuth2User updateAccountConfig(OAuth2UserRequest request, OAuth2User user) {
		String registrationId = request.getClientRegistration().getRegistrationId();
		OAuth2UserInfo userInfo = OAuth2UserInfoFactory.of(registrationId, user.getAttributes());
		AuthProviderType authority = AuthProviderType.of(registrationId);
		
		Account account = principalService.loadAccountByExternalId(userInfo.getId(), authority);
		Account signup = signupService.getCurrentSignupAccount();
		
		if(account == null && signup != null) {
			// setup new account
			setupSignupAccount(signup, request, userInfo);
			account = signup;
			
		} else if(account != null && signup == null) {
			// validate user
			validateExisting(account, request, userInfo);
		
		} else if(account != null && signup != null) {
			// account already exists
			// TODO: notify user account exists, and to contact support
			//			could be because of duplicate user/person (admin's fault)
			//			or because email is tied to another account, check if conflicting accounts have unverified emails
			
		} else if(account == null && signup == null) {
			// no account
			// TODO: notify user they do not have an account
			//			if looking to sign-up please talk with an ALR member
			//			or check you're inbox for an invitation link
		}

		return principalService.loadFromAccount(account);
	}

	private boolean setupSignupAccount(Account account, OAuth2UserRequest request, OAuth2UserInfo userInfo) {
		if(account.getAuthService() != null) {
			// TODO: account has already been signed up
			//			prompt user with error message, offer to login
			//			end sign-up process
//			throw new SigupException
			throw new IllegalArgumentException("Account already registered");
		}
		
		AuthProviderType authority = AuthProviderType.of(request.getClientRegistration().getRegistrationId());
		account.withExternalAuthentication(authority, userInfo.getId());
//		account.setImageUrl(oAuth2UserInfo.getImageUrl());
		signupService.completeSignup(account);
		
		return true;
	}

	private boolean validateExisting(Account account, OAuth2UserRequest request, OAuth2UserInfo userInfo) {
		return true;
	}
}
