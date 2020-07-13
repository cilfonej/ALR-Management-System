package edu.wit.alr.web.security.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.sun.xml.bind.v2.TODO;

import edu.wit.alr.database.model.Account;
import edu.wit.alr.web.security.AccountDetailsService;
import edu.wit.alr.web.security.oauth2.userinfo.OAuth2UserInfo;
import edu.wit.alr.web.security.oauth2.userinfo.OAuth2UserInfoFactory;

/**
 * 	Service used for converting an {@link OAuth2UserRequest Authentication-Request} into
 * 	a {@link AuthenticatedPrincipal User} to be used by Spring's authentication/security system.
 * 	
 * 	<p>
 * 	This Service is also responsible for finalizing the {@link Account} configuration for a person
 * 	who wishes to log-in via an external OAuth2 service. An {@link Account} must have already be loaded 
 * 	into the current session for this to be achieved. This is most often done by the {@link TODO RegistrationController}
 * 	via following an "sign-up invitation" link.
 * 
 * 	@author cilfonej
 */

@Service
public class OAuth2UserProvider extends DefaultOAuth2UserService {

	@Autowired
	private AccountDetailsService accountService;

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
//		
//		if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
//			throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
//		}
//
//		Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
//		User user;
//		if(userOptional.isPresent()) {
//			user = userOptional.get();
//			if(!user.getProvider()
//					.equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
//				throw new OAuth2AuthenticationProcessingException(
//						"Looks like you're signed up with " + user.getProvider() + " account. Please use your "
//								+ user.getProvider() + " account to login.");
//			}
//			user = updateExistingUser(user, oAuth2UserInfo);
//		} else {
//			user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
//		}

		return accountService.loadUserByExternalId(userInfo.getId());
	}

//	private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
//		User user = new User();
//
//		user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
//		user.setProviderId(oAuth2UserInfo.getId());
//		user.setName(oAuth2UserInfo.getName());
//		user.setEmail(oAuth2UserInfo.getEmail());
//		user.setImageUrl(oAuth2UserInfo.getImageUrl());
//		return userRepository.save(user);
//	}
//
//	private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
//		existingUser.setName(oAuth2UserInfo.getName());
//		existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
//		return userRepository.save(existingUser);
//	}
}
